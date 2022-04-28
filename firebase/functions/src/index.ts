import { randomUUID } from "crypto";
import * as admin from "firebase-admin";
import { getFirestore, Timestamp } from 'firebase-admin/firestore';
import { https, region } from "firebase-functions";


admin.initializeApp();


const eu = region("europe-west1");

export const queue = eu.https.onCall(async (data, context) => {
  const game_mode_s = data.game_mode as string | undefined;
  if (game_mode_s === undefined) {
    throw new https.HttpsError("invalid-argument", "game_mode is required");
  }
  const game_mode = new GameMode(game_mode_s);

  const gamemodes = await get_gamemodes();

  if (gamemodes.map(gm => gm.toString()).indexOf(game_mode.name) === -1) {
    throw new https.HttpsError("invalid-argument", `Provided gamemode \`${game_mode.toString()
      }\` is invalid because it is not in the list of available gamemodes : ${gamemodes.map(gm => gm.toString()).join(", ")}`);
  }

  const games = await prod.collection("games")
    .where("game_mode", "==", game_mode.toString())
    .where("player_count", "<", game_mode.max_player_count)
    .where("done", "==", false)
    .orderBy("player_count", "desc")
    .orderBy("timestamp", 'asc')
    .get();

  const games_im_in =
    await prod.collection("games")
      .where("done", "==", false)
      .where("players", "array-contains", context.auth!.uid).limit(1).get();

  if (games_im_in.size > 0) {
    throw new https.HttpsError("invalid-argument", "You are already in a game");
  }

  let game: Game;

  if (games.empty) {
    // Create a new game
    game = createGame(game_mode);
  } else {
    // Add player to existing game
    game = games.docs[0].data() as Game;
  }

  addPlayer(game, context.auth!.uid);

  if (game.player_count > game_mode.max_player_count) {
    throw new https.HttpsError("invalid-argument", "Too many players");
  }

  game.started = game.player_count === game_mode.max_player_count;
  //! TRANSACTION !!!
  await prod.collection("games").doc(game.id).set(game);
  return game.id;
});


/*
to play with friends in a game, we need to call the following endpoint: /invite with the following parameters:

userId: the user id of the user to invite
Upon receiving the invitation, the user will be notified by a notification.

Server-side
When the HTTPS request is received, a new document is created in the users/{userId}/invitations/ collection:

{
  "game": "game_uid",
  "timestamp": "2020-01-01T00:00:00.000Z",
  "inviter": "user_uid",
  "invitation_id": "invitation_id"
}*/

export const invite_player = eu.https.onCall(async (data, context) => {
  const user_id = data.user_id as string | undefined;
  const game_mode = data.game_mode as string | undefined;
  if (user_id === undefined) {
    throw new https.HttpsError("invalid-argument", "user_id is required");
  }
  if (game_mode === undefined) {
    throw new https.HttpsError("invalid-argument", "game_mode is required");
  }

  const gamemodes = await get_gamemodes();

  if (!gamemodes.some(gm => gm.name === game_mode)) {
    throw new https.HttpsError("invalid-argument", `Provided gamemode \`${game_mode}\` is invalid`);
  }

  const user = await prod.collection("users").doc(user_id).get();
  if (!user.exists) {
    throw new https.HttpsError("not-found", "User not found");
  }

  const game = createGame(new GameMode(game_mode));
  addPlayer(game, context.auth!.uid);

  const invitation = {
    game: game.id,
    timestamp: Timestamp.now(),
    inviter: context.auth!.uid,
    invitation_id: randomUUID()
  };

  await prod.collection("users").doc(user_id).collection("invitations").doc(invitation.invitation_id).set(invitation);
  return game.id;
})


export const accept_invitation = eu.https.onCall(async (data, context) => {
  const invitation_id = data.invitation_id as string | undefined;

  if (invitation_id === undefined) {
    throw new https.HttpsError("invalid-argument", "invitation_id is required");
  }

  const invitation = await prod.collection("users").doc(context.auth!.uid).collection("invitations").doc(invitation_id).get();
  if (!invitation.exists) {
    throw new https.HttpsError("not-found", "Invitation not found");
  }

  const game = await prod.collection("games").doc(invitation.data()!.game).get();
  if (!game.exists) {
    throw new https.HttpsError("not-found", "Game not found");
  }

  if (game.data()!.player_count >= new GameMode(game.data()!.game_mode).max_player_count) {
    throw new https.HttpsError("invalid-argument", "Game is full");
  }

  addPlayer(game.data() as Game, context.auth!.uid);
  //! TRANSACTION !!!
  await prod.collection("games").doc(game.id).set(game.data() as Game);
  await prod.collection("users").doc(context.auth!.uid).collection("invitations").doc(invitation_id).delete();
  return game.id;
})

async function get_gamemodes(): Promise<GameMode[]> {
  const gamemodes = await prod.collection("global").doc("gamemodes").get();
  if (!gamemodes.exists) {
    throw new https.HttpsError("not-found", "Gamemodes not found");
  }
  return (gamemodes.data()!.gamemodes as string[]).map(gm => new GameMode(gm));
}

class GameMode {
  max_player_count: number;
  game_type: string;
  rounds: number;
  time_limit: number;

  // "P:5,G:PC,R:3,T:0", //5 players, against computer, 3 rounds, 0 time limit (no time limit)
  constructor(public name: string) {
    const parts = name.split(",").map(p => p.trim().split(":", 2) as [string, string]);
    const map = new Map<string, string>(parts);
    this.max_player_count = parseInt(map.get("P")!);
    this.game_type = map.get("G")!;
    this.rounds = parseInt(map.get("R")!);
    this.time_limit = parseInt(map.get("T")!);
  }

  toString() {
    // Properties in the format "G:PVP,P:2,R:3,T:0"
    return `G:${this.game_type},P:${this.max_player_count},R:${this.rounds},T:${this.time_limit}`;
  }
}

const prod = getFirestore();

function createGame(game_mode: GameMode): Game {
  return {
    id: randomUUID(),
    game_mode: game_mode.name,
    player_count: 0,
    timestamp: Timestamp.now(),
    players: [],
    rounds: {
      "0": {
        hands: {},
        timestamp: Timestamp.now(),
      }
    },
    current_round: 0,
    started: false,
    done: false
  };
}

function addPlayer(game: Game, player: string) {
  game.players.push(player);
  game.player_count++;
  return game;
}

interface Game {
  id: string;
  game_mode: string;
  player_count: number;
  timestamp: Timestamp;
  players: string[];
  rounds: round_map;
  current_round: number;
  started: boolean;
  done: boolean;
}

// an interface with any key and any value
interface round_map {
  [key: string]: Round;
}

interface hands_map {
  [key: string]: Hand;
}

interface Round {
  timestamp: Timestamp;
  hands: hands_map;
}

enum Hand {
  ROCK, PAPER, SCISSORS, NONE
}