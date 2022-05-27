package ch.epfl.sweng.rps.models.xbstract

import ch.epfl.sweng.rps.models.remote.User

abstract class AbstractUser {
    abstract val username: String?
    abstract val uid: String
    abstract val games_history_privacy: String
    abstract val has_profile_photo: Boolean

    fun gamesHistoryPrivacyEnum(): User.Privacy = User.Privacy.valueOf(games_history_privacy)
}