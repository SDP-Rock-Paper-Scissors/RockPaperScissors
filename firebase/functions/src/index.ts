import { randomUUID } from "crypto";
import { getFirestore, Timestamp } from 'firebase-admin/firestore';
import { https, region } from "firebase-functions";

const eu = region("europe-west1");

export const queue = eu.https.onCall(async (data, context) => {
  const game_mode_s = data.game_mode as string | undefined;
  if (game_mode_s === undefined) {
    throw new https.HttpsError("invalid-argument", "game_mode is required");
  }

  const gamemodes = await get_gamemodes();

  if (!gamemodes.some(gm => gm.name === game_mode.name)) {
    throw new https.HttpsError("invalid-argument", `Provided gamemode \`${game_mode_s}\` is invalid`);
  }
  const game_mode = new GameMode(game_mode_s);

  const games = await prod.collection("games")
    .where("game_mode", "==", game_mode)
    .where("player_count", "<", game_mode.max_player_count)
    .where("done", "==", false)
    .orderBy("player_count", "desc")
    .orderBy("timestamp", 'asc')

    .get();

  let game: Game;

  if (games.empty) {
    // Create a new game
    game = createGame(game_mode);
  } else {
    // Add player to existing game
    game = games.docs[0].data() as Game;
  }

  addPlayer(game, context.auth!.uid);

  if (game.player_count === game_mode.max_player_count) {
    // start the game
    const round = createRound(game);
    await prod.collection("games").doc(game.id).collection("rounds").doc(round.id).set(round);
    game.rounds.push(round.id);
  } else if (game.player_count > game_mode.max_player_count) {
    throw new https.HttpsError("invalid-argument", "Too many players");
  }

  await prod.collection("games").doc(game.id).set(game);
  return game.id;
});

async function get_gamemodes(): Promise<GameMode[]> {
  const gamemodes = await prod.collection("global").doc("gamemodes").get();
  return (gamemodes.data()!.gamemodes as string[]).map(gm => new GameMode(gm));
}

class GameMode {
  max_player_count: number;
  game_type: GameType;
  rounds: number;
  time_limit: number;

  // "P:5,G:PC,R:3,T:0", //5 players, against computer, 3 rounds, 0 time limit (no time limit)
  constructor(public name: string) {
    const parts = name.split(",").map(p => p.trim().split(":", 2) as [string, string]);
    const map = new Map<string, string>(parts);
    this.max_player_count = parseInt(map.get("P")!);
    this.game_type = GameType[map.get("G")! as keyof typeof GameType];
    this.rounds = parseInt(map.get("R")!);
    this.time_limit = parseInt(map.get("T")!);
  }
}
enum GameType {
  pc, pvp, local
}

const prod = getFirestore().doc('env/prod/');



function createGame(game_mode: GameMode): Game {
  return {
    id: randomUUID(),
    game_mode: game_mode.name,
    player_count: game_mode.max_player_count,
    timestamp: Timestamp.now(),
    players: [],
    rounds: []
  };
}

function createRound(game: Game): Round {
  return {
    id: randomUUID(),
    game_id: game.id,
    timestamp: Timestamp.now(),
    hands: new Map<string, Hand>(),
  }
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
  rounds: string[];
}

interface Round {
  id: string;
  timestamp: Timestamp;
  game_id: string;
  hands: Map<string, Hand>;
}

enum Hand {
  ROCK, PAPER, SCISSORS, NONE
}