package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.serialization.Extensions.toJsonElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@Serializable
data class User(
    @SerialName(User.CONSTANTS.USERNAME)
    val username: String? = null,

    val uid: String,

    @SerialName(User.CONSTANTS.GAMES_HISTORY)
    val gamesHistoryPrivacy: Privacy = Privacy.PRIVATE,

    val friends: List<String>,

    @SerialName(User.CONSTANTS.HAS_PROFILE_PHOTO)
    val hasProfilePhoto: Boolean = false,

    val email: String?
) {
    private interface CONSTANTS {
        companion object {
            const val USERNAME = "username"
            const val GAMES_HISTORY = "games_history_privacy"
            const val HAS_PROFILE_PHOTO = "has_profile_photo"
        }
    }


    enum class Field(val value: String) {
        USERNAME(User.CONSTANTS.USERNAME),
        GAMES_HISTORY_POLICY(User.CONSTANTS.GAMES_HISTORY),
        HAS_PROFILE_PHOTO(User.CONSTANTS.HAS_PROFILE_PHOTO),
        ;
    }

    enum class Privacy {
        PUBLIC, PRIVATE, FRIENDS_ONLY
    }

    companion object {
        fun fromJsonString(json: String): User {
            return Json.decodeFromString(User.serializer(), json)
        }

        fun fromJson(json: Map<String, Any>): User {
            return Json.decodeFromJsonElement(User.serializer(), json.toJsonElement())
        }
    }

    fun toJSON(): Map<String, Any> {
        return Json.encodeToJsonElement(serializer(), this) as JsonObject
    }
}

