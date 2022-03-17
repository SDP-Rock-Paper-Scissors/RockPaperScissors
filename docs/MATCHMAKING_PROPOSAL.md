This proposal details the specifications for the matchmaking in our app.
## Overview
### 1. Queue

1. The user presses the PLAY button, making him join a queue for the chosen gamemode.
2. We create a document in the `queue/` collection in Firestore, indicating the player is queuing up.
The document needs to contain the gamemode as well as the `uid` of the player queuing up.
```jsonc
{
  "player_uid": "my_user_uid",
  "gamemode": "5P,PC,3R,0T", //5 players, against computer, 3 rounds, 0 time limit (no time limit)
  "timestamp": "2020-01-01T00:00:00.000Z",
  "lobby": null
}
```

### 2. The game - server-side

The server consists of Firebase Functions listenning for creations of documents in the `queue/` collection.
1. When a document is created, we check if there are enough players in the queue to start a game in the chosen gamemode.
2. If there are enough players, we create a game document in the `games/` collection.
3. The game document needs to contain the gamemode as well as the `uid` of the players in the game.
4. We update the document in the `queue/` collection to indicate the game has started by setting the `lobby` field to the game document's `id`.

The game document `/game/{gameId}` should look like this:
```jsonc
{
  "mode": "5P,PC,3R,0T",
  "timestamp": "2020-01-01T00:00:00.000Z",
  "rounds": [],
  "players": [/*players*/]
}
```

### 3. Game start - client-side

Now that we have a game document, we can start the game.
The client is aware the game has started as soon as the `lobby` field of the document in the `queue/` collection is set to the game document's `id`.
It can now access the game document in the `games/` collection.
The game document contains the gamemode as well as the `uid` of the players in the game, so the client can list the players in the game.

### 4. Ready check (Optional)
To make sure players are ready, each player needs to press the READY button.
This will update the `ready` map of the game document to `true` for the player. (The player's uid is the key.)

If after a certain amount of time, the game is not ready, the game is cancelled:
- The document in the queue is marked as done by setting the `done` field to `true`.
- The game document is marked as done by setting the `done` field to `true`.

### 5. Round
Once every player is ready, server adds a round document in the `rounds/` collection of the game document `/games/{gameId}/rounds/{roundId}`:
```jsonc
{
  "uid": "round_uid",
  "timestamp": "2020-01-01T00:00:00.000Z",
  "players": [/*players*/],
  "game": "game_uid",
  "hands": {}
}
```
It also adds the round's `id` to the `rounds` field of the game document.

Each player pick their hand (`ROCK`, `PAPER` or `SCISSORS`). The client update the `hands` map of the round document to reflect the player's hand. Say `user1` picked `ROCK`, `user2` picked `PAPER` and `user3` picked `SCISSORS`:

```jsonc
{
  "uid": "round_uid",
  "timestamp": "2020-01-01T00:00:00.000Z",
  "players": [/*players*/],
  "game": "game_uid",
  "hands": {
    "user1": "ROCK",
    "user2": "PAPER",
    "user3": "SCISSORS"
  }
}
```

A round is over when each player in `players` has a value in the `hands` map.

The winner of a round is determined by comparing the hands of the players. The winner is the player with the highest score. It is computed like so:

1. Every player gets a score of 0 at the beginning of the round.
2. Each player is confronted to every other player.
    - If the player's hand **wins** against the other player's hand, the player **gets a point**.
    - If the player's hand **loses** against the other player's hand, the player **loses a point**.
    - If the player's hand **ties** with the other player's hand, **no points are awarded**.


### 6. Next Round
Once a round is over, the server adds a new round document in the `rounds/` collection of the game document `/games/{gameId}/rounds/{roundId}`:

### 7. Game Over
Once all rounds are over, the game is over.
The game document is marked as done by setting the `done` field to `true`.

The winner is the player which won the most rounds.

When a game is set to done, a user can't write to the game document anymore.

A list of a users past games can be thus obtained by querying the `games/` where `players` contains the user's uid and done is `true`:
```kotlin
val gamesPlayed = gamesCollection.whereArrayContains("players", user.uid).whereEqualTo("done", true).get()
```

## Security

As a matter of good practice as well as security, we want to have strict access control on the data.

Here is the list of rules:

Resource                            | Permission | Condition
------------------------------------|------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
`queue/{doc_id}`                    | `Read`     | `doc.player_uid == user.uid`
`queue/{doc_id}`                    | `Create`   | `isGameModeValid(doc.mode)`, `doc.done == false`
`games/{doc_id}`                    | `Read`     | -
`games/{game_id}/rounds/{round_id}` | `Read`     | -
`games/{game_id}/rounds/{round_id}` | `Update`   | `doc.players.contains(user.uid)`, `doc.done == false`, `doc.hands.containsKey(user.uid) == false`, `request.update.data.hands.length == 1`, `request.update.data.hands.containsKey(user.uid)`

All other operations on the aforementioned resources are forbidden.


## Notes
- Using Firebase, we can listen for document changes, allowing us to essentially be "notified" by server for different events. That allows us to have a "real-time" experience even if we don't have a proper backend.
- When refering to players, we assume their might be more than two players in a game. We will start by supporting two players and then expand to more possibly.
- To make it easier and avoid spreading users across a too high variety of gamemodes, we should choose a subset of gamemodes from combinations of options (number of players, number of rounds, time limit).