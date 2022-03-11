package ch.epfl.sweng.rps.models

import ch.epfl.sweng.rps.models.User.Privacy
import org.junit.Assert.assertEquals
import org.junit.Test

class UserTest {
    @Test
    fun testUser() {
        val user =
            User("username", "uid", Privacy.PUBLIC, listOf("friend1", "friend2"), true, "email")
        assertEquals("username", user.username)
        assertEquals("uid", user.uid)
        assertEquals(Privacy.PUBLIC, user.gamesHistoryPrivacy)
        assertEquals(listOf("friend1", "friend2"), user.friends)
        assertEquals(true, user.hasProfilePhoto)
        assertEquals("email", user.email)
    }
}