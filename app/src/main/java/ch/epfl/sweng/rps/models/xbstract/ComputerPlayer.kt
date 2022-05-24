package ch.epfl.sweng.rps.models.xbstract

import ch.epfl.sweng.rps.models.remote.Hand

abstract class ComputerPlayer(
    username: String? = "",
    override val uid: String,
    games_history_privacy: String = "",
    has_profile_photo: Boolean = false,
) : AbstractUser(username, uid, games_history_privacy, has_profile_photo) {
    /**
     * @return one of the possible moves (represented by a class e.g. Hand)
     */
    abstract fun makeMove(): Hand
}