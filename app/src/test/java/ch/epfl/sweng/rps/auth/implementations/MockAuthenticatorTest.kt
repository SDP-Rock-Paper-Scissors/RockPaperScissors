package ch.epfl.sweng.rps.auth.implementations

import ch.epfl.sweng.rps.auth.Authenticator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MockAuthenticatorTest {
    @Test
    fun MockedAuthenticatorCallbackIsCalled(){
        val callback = {res:String -> assertEquals(res, "google")}
        val authenticator:Authenticator = object : Authenticator(callback) {
            override fun signInWithGoogle() {
                callback("google")
            }

        }
        authenticator.signInWithGoogle()
    }
}