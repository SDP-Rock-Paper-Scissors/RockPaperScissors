package ch.epfl.sweng.rps.db

import ch.epfl.sweng.rps.models.User

sealed class FirebaseHelper {
    companion object {
        fun processUserArguments(vararg pairs: Pair<User.Field, Any>): Map<String, Any> {
            return pairs.associate { t -> t.first.value to t.second }
        }


        fun userFrom(uid: String, name: String, email: String?): User {
            return User(
                email = email,
                username = name,
                gamesHistoryPrivacy = User.Privacy.PUBLIC,
                hasProfilePhoto = false,
                uid = uid
            )

        }
    }
}