package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.User.Privacy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserTest {
    @Test
    fun testUser() {
        val user =
            User("username", "uid", Privacy.PUBLIC, true, "email")
        assertEquals("username", user.username)
        assertEquals("uid", user.uid)
        assertEquals(Privacy.PUBLIC, user.gamesHistoryPrivacy)
        assertEquals(true, user.hasProfilePhoto)
        assertEquals("email", user.email)
    }
}