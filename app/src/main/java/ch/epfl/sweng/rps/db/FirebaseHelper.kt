package ch.epfl.sweng.rps.db

import ch.epfl.sweng.rps.models.User

sealed class FirebaseHelper {
    companion object {
        fun processUserArguments(vararg pairs: Pair<User.Field, Any>): Map<String, Any> {
            return pairs.associate { t -> Pair(t.first.value, t.second) }
        }
    }
}