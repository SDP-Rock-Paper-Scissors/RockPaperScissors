package ch.epfl.sweng.rps.models.xbstract

import ch.epfl.sweng.rps.models.User

abstract class AbstractUser {
    abstract val username: String?
    abstract val uid: String
    abstract val games_history_privacy: String
    abstract val has_profile_photo: Boolean

    open fun gamesHistoryPrivacyEnum(): User.Privacy = User.Privacy.valueOf(games_history_privacy)
}