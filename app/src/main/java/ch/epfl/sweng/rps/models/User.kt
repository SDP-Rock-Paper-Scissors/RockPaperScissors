package ch.epfl.sweng.rps.models

/**
 *
 */
data class User(
    val username: String?,

    val uid: String,

    val gamesHistoryPrivacy: Privacy,

    val hasProfilePhoto: Boolean,

    val email: String?
) {

    fun deepCopy(
        username: String? = this.username,
        uid: String = this.uid,
        gamesHistoryPrivacy: Privacy = this.gamesHistoryPrivacy,
        hasProfilePhoto: Boolean = this.hasProfilePhoto,
        email: String? = this.email
    ): User {
        return User(username, uid, gamesHistoryPrivacy, hasProfilePhoto, email)
    }

    enum class Field(val value: String) {
        USERNAME("username"),
        GAMES_HISTORY_PRIVACY("games_history_privacy"),
        HAS_PROFILE_PHOTO("has_profile_photo"),
        EMAIL("email"),
        // UID("uid")
    }

    enum class Privacy {
        PUBLIC, PRIVATE, FRIENDS_ONLY
    }

}

