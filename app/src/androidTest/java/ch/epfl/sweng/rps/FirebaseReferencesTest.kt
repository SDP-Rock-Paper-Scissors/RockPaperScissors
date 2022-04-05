package ch.epfl.sweng.rps

import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.db.FirebaseReferences
import com.google.firebase.FirebaseApp
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FirebaseReferencesTest {

    @Before
    fun setUp() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun usersFriendRequestOfUid() {
        assertEquals(
            "env/dev/users/{uid}/friend_requests",
            FirebaseReferences(Env.Dev).usersFriendRequestOfUid("{uid}").path
        )
    }

    @Test
    fun getUsersCollection() {
        assertEquals(
            "env/dev/users",
            FirebaseReferences(Env.Dev).usersCollection.path
        )
    }

    @Test
    fun getProfilePicturesFolder() {
        assertEquals(
            "/env/dev/profile_pictures",
            FirebaseReferences(Env.Dev).profilePicturesFolder.path
        )
    }

    @Test
    fun getEnv() {
        val repo = FirebaseReferences(Env.Dev)
        assertEquals(Env.Dev, repo.env)
    }
}