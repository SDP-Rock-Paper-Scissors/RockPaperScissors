package ch.epfl.sweng.rps.auth.implementations

import ch.epfl.sweng.rps.auth.Authenticator
import org.junit.Test
import org.junit.Assert
import org.mockito.Mockito

class MockAuthenticatorTest {
    @Test
    fun MockedAuthenticatorCallbackIsCalled(){
        val callback = {res:String -> Assert.assertEquals(res, "google")}
        val authenticator:Authenticator = object : Authenticator(callback) {
            override fun signInWithGoogle() {
                callback("google")
            }

        }
        authenticator.signInWithGoogle()
    }
}