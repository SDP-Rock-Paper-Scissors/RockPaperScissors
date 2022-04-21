package ch.epfl.sweng.rps.logic

import ch.epfl.sweng.rps.logic.Repository.UserNotLoggedIn
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserNotLoggedInTest {
    @Test
    fun testRepositoryException() {
        assertThrows(UserNotLoggedIn::class.java) {
            throw UserNotLoggedIn("test")
        }

        assertThrows(UserNotLoggedIn::class.java) {
            throw UserNotLoggedIn("test", Exception())
        }

        assertThrows(UserNotLoggedIn::class.java) {
            throw UserNotLoggedIn(Exception())
        }

        assertThrows(UserNotLoggedIn::class.java) {
            throw UserNotLoggedIn()
        }
    }
}