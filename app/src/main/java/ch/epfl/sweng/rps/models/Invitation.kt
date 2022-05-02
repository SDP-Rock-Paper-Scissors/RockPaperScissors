package ch.epfl.sweng.rps.models

import com.google.firebase.Timestamp

/*
* const invitation = {
    game: game.id,
    timestamp: Timestamp.now(),
    inviter: context.auth!.uid,
    invitation_id: randomUUID()
  }
  * */
data class Invitation(
    val game_id: String,
    val timestamp: Timestamp,
    val from: String,
    val id: String
)