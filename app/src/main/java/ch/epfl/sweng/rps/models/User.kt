package ch.epfl.sweng.rps.models

/**
 *
 */
data class User(
    val username: String?,

    val uid: String,

    val gamesHistoryPrivacy: Privacy,

    val friends: List<String>,

    val hasProfilePhoto: Boolean,

    val email: String?
) {

    enum class Field(val value: String) {
        USERNAME("username"),
        GAMES_HISTORY_POLICY("games_history_privacy"),
        HAS_PROFILE_PHOTO("has_profile_photo"),
        EMAIL("email"),
        FRIENDS("friends"),
        UID("uid")
    }

    enum class Privacy {
        PUBLIC, PRIVATE, FRIENDS_ONLY
    }

}

