package ch.epfl.sweng.rps.models

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserTest {
    @Test
    fun testUser() {
        val user =
            User("username", "uid", "PUBLIC", true, "email")
        assertEquals("username", user.username)
        assertEquals("uid", user.uid)
        assertEquals("PUBLIC", user.games_history_privacy)
        assertEquals(true, user.has_profile_photo)
        assertEquals("email", user.email)
    }
}