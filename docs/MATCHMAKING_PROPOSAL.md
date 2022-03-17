This proposal details the specifications for the matchmaking in our app.
## Overview
### 1. Queue

The user presses the PLAY button, making him join a queue for the chosen gamemode.

To indicate the server we are queueing for a game, we call the HTTPS endpoint `/queue` with the following parameters:

* `game_mode`: the game mode we are queueing for

*Note: We do not need to provide the user id, as HTTPS cloud functions authenticate the user with their already authenticated firebase identity, which allows to obtain the user uid without explicitly providing it. It also allows to have some security as only authenticated users can call this function. [Source](https://firebase.google.com/docs/functions/callable)*
### 2. The game - server-side

Once the server receives a request to queue, it acts as follows:

1. Looks for games that are not full and have the same game mode.
2. If there are no such games, creates a new game with the same game mode.
3. Adds the user to the game.
4. If the game is full, starts the game.
5. If the game is not full, waits for the other players to join.
6. Either way, returns the game id to the user.

The game document `/game/{gameId}` looks like this:
```jsonc
// game.json

{
  "mode": "5P,PC,3R,0T",
  "timestamp": "2020-01-01T00:00:00.000Z",
  "rounds": [],
  "players": [/*players*/],
  "done": false,
  "users_state": {
    "user1": "ABSENT",
    "user2": "READY",
    "user3": "JOINED,
  }
}
```

In the game document, the users can have 3 different states:
* `ABSENT`: the user is not in the game
* `JOINED`: the user has joined the game, but is not ready yet
* `READY`: the user has joined the game and is ready to start the game

### 3. Game start - client-side

Now that we have a game id, we can retrieve the game document:
```
/games/{gameId}
```
and listen to the game document for changes.
### 4. Ready check (Optional)
To make sure players are ready, each player needs to press the READY button.
This will update the `users_state` map of the game document to `READY` for the player. (The player's uid is the key.)

If after a certain amount of time, the game is not ready, the game is cancelled:
- The document in the queue is marked as done by setting the `done` field to `true`.
- The game document is marked as done by setting the `done` field to `true`.

### 5. Round
Once every player is ready, server adds a round document in the `rounds/` collection of the game document `/games/{gameId}/rounds/{roundId}`:
```jsonc
// round.json

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
// round.json

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
## States

Games have 3 implicit states, which are:

State       | Condition
------------|---------------------------------------------------------
NOT_STARTED | When not all players are ready
PLAYING     | When players are all ready, and there's a round present.
FINISHED    | When the `done` field is set to `true`.
## Security

As a matter of good practice as well as security, we want to have strict access control on the data.

Here is the list of rules:

Resource                            | Permission | Condition
------------------------------------|------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
`games/{doc_id}`                    | `Read`     | -
`games/{game_id}/rounds/{round_id}` | `Read`     | -
`games/{game_id}/rounds/{round_id}` | `Update`   | `doc.players.contains(user.uid)`, `doc.done == false`, `doc.hands.containsKey(user.uid) == false`, `request.update.data.hands.length == 1`, `request.update.data.hands.containsKey(user.uid)`

All other operations on the aforementioned resources are forbidden.

## Playing with friends

To play with friends in a game, we need to call the following endpoint: `/invite` with the following parameters:
* `userId`: the user id of the user to invite

Upon receiving the invitation, the user will be notified by a notification.

### Server-side

When the HTTPS request is received, a new document is created in the `users/{userId}/invitations/` collection:

```jsonc
{
  "game": "game_uid",
  "timestamp": "2020-01-01T00:00:00.000Z",
  "inviter": "user_uid",
  "invitation_id": "invitation_id"
}
```

Upon receiving the invitation, the invited user will now be able to join the game by calling the following endpoint: `/join` with the following parameters:
* `invitation_id`: the id of the invitation

The server will then add the user to the game document.

## Notes
- Using Firebase, we can listen for document changes, allowing us to essentially be "notified" by server for different events. That allows us to have a "real-time" experience even if we don't have a proper backend.
- When refering to players, we assume their might be more than two players in a game. We will start by supporting two players and then expand to more possibly.
- To make it easier and avoid spreading users across a too high variety of gamemodes, we should choose a subset of gamemodes from combinations of options (number of players, number of rounds, time limit).