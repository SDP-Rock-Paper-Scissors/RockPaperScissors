package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.User.Privacy
import org.junit.Assert.assertEquals
import org.junit.Test

class UserTest {
    @Test
    fun testUser() {
        val user =
            User("username", "uid", "PUBLIC", true, "email")
        assertEquals("username", user.username)
        assertEquals("uid", user.uid)
        assertEquals("PUBLIC", user.gamesHistoryPrivacy)
        assertEquals(true, user.hasProfilePhoto)
        assertEquals("email", user.email)
    }
}