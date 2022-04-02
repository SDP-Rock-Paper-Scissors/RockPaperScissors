package ch.epfl.sweng.rps.auth.implementations

import ch.epfl.sweng.rps.auth.Authenticator
import ch.epfl.sweng.rps.models.User
import org.junit.Test
import org.junit.Assert
import org.mockito.Mockito

class MockAuthenticatorTest {
    @Test
    fun MockedAuthenticatorCallbackIsCalled(){
        val callback = {res:User -> Assert.assertEquals(res.username, "username")}
        val authenticator:Authenticator = object : Authenticator(callback) {
            override fun signInWithGoogle() {
                callback(User("username","uid","PRIVATE",false,"rps@epfl.ch",null))
            }

        }
        authenticator.signInWithGoogle()
    }
}