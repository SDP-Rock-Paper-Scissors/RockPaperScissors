package ch.epfl.sweng.rps.models.xbstract

import ch.epfl.sweng.rps.models.remote.User

abstract class AbstractUser(
    open val username: String? = "",
    open val uid: String = "",
    open val games_history_privacy: String = "",
    open val has_profile_photo: Boolean = false,
) {
    fun gamesHistoryPrivacyEnum(): User.Privacy = User.Privacy.valueOf(games_history_privacy)
}