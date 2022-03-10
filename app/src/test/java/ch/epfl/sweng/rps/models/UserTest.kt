package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.serialization.Extensions.toJsonElement
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class UserTest {
    @Test
    fun testSerialization() {
        val user = User(
            username = "Gaëtan S.",
            email = "gaetan.schwartz@epfl.ch",
            friends = listOf(),
            uid = "gaetan_s"
        )
        val s = user.toJSON()
        print(s)
        val user2 = User.fromJson(s)
        assertEquals(user2, user)
    }

    @Test
    fun testSerialization2() {
        val user = User(
            username = "Gaëtan S.",
            email = "gaetan.schwartz@epfl.ch",
            friends = listOf(),
            uid = "gaetan_s"
        )
        val userMap = hashMapOf(
            "username" to user.username,
            "email" to user.email,
            "friends" to user.friends,
            "uid" to user.uid,
            User.Field.GAMES_HISTORY_POLICY.value to user.gamesHistoryPrivacy,
            User.Field.HAS_PROFILE_PHOTO.value to user.hasProfilePhoto,
        )
        print(userMap.toJsonElement())
        val s = Json.encodeToString(userMap.toJsonElement())
        print(s)
        assertEquals(
            user,
            Json.decodeFromJsonElement(User.serializer(), userMap.toJsonElement())
        )
    }
}