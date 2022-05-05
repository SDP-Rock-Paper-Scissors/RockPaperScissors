package ch.epfl.sweng.rps.models

/**
 *
 */
data class User(
    val username: String? = "",

    val uid: String = "",

    val games_history_privacy: String = "",

    val has_profile_photo: Boolean = false,

    val email: String? = "",
) {
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

    fun gamesHistoryPrivacyEnum(): Privacy = Privacy.valueOf(games_history_privacy)
}

