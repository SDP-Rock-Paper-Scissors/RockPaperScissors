package ch.epfl.sweng.rps.auth.implementations

import ch.epfl.sweng.rps.auth.Authenticator
import ch.epfl.sweng.rps.models.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MockAuthenticatorTest {
    @Test
    fun mockedAuthenticatorCallbackIsCalled() {
        val callback = { res: User -> assertEquals(res.username, "username") }
        val authenticator: Authenticator = object : Authenticator(callback) {
            override fun signInWithGoogle() {
                callback(User("username", "uid", "PRIVATE", false, "rps@epfl.ch", null))
            }

        }
        authenticator.signInWithGoogle()
    }
}