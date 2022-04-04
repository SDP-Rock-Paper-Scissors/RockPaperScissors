package ch.epfl.sweng.rps.models

/**
 *
 */
data class User(
    val username: String? = "",

    val uid: String = "",

    val gamesHistoryPrivacy: String = "",

    val hasProfilePhoto: Boolean = false,

    val email: String? = "",

    val matchesList: List<String>? = null
) {

    fun deepCopy(
        username: String? = this.username,
        uid: String = this.uid,
        gamesHistoryPrivacy: String = this.gamesHistoryPrivacy,
        hasProfilePhoto: Boolean = this.hasProfilePhoto,
        email: String? = this.email,
        matchesList: List<String>? = this.matchesList
    ): User {
        return User(username, uid, gamesHistoryPrivacy, hasProfilePhoto, email, matchesList)
    }

    enum class Field(val value: String) {
        USERNAME("username"),
        GAMES_HISTORY_PRIVACY("games_history_privacy"),
        HAS_PROFILE_PHOTO("has_profile_photo"),
        EMAIL("email"),
        UID("uid"),
        MATCHESLIST("matchesList")
    }

    enum class Privacy {
        PUBLIC, PRIVATE, FRIENDS_ONLY
    }

}

