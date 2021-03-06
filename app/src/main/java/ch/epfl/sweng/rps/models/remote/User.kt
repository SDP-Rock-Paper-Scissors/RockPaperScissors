package ch.epfl.sweng.rps.models.remote

import ch.epfl.sweng.rps.models.xbstract.AbstractUser

/**
 *
 */
data class User(
    override val username: String? = "",

    override val uid: String = "",

    override val games_history_privacy: String = "",

    override val has_profile_photo: Boolean = false,

    val email: String? = "",
) : AbstractUser() {


    enum class Field(val value: String) {
        USERNAME("username"),
        GAMES_HISTORY_PRIVACY("games_history_privacy"),
        HAS_PROFILE_PHOTO("has_profile_photo"),
        EMAIL("email"),
        UID("uid"),
    }

    enum class Privacy {
        PUBLIC, PRIVATE, FRIENDS_ONLY
    }
}

