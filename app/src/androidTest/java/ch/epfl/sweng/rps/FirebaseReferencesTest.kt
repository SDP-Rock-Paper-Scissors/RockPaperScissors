package ch.epfl.sweng.rps

import androidx.test.platform.app.InstrumentationRegistry
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
            "users/{uid}/friend_requests",
            FirebaseReferences().usersFriendRequestOfUid("{uid}").path
        )
    }

    @Test
    fun getUsersCollection() {
        assertEquals(
            "users",
            FirebaseReferences().usersCollection.path
        )
    }

    @Test
    fun getProfilePicturesFolder() {
        assertEquals(
            "/profile_pictures",
            FirebaseReferences().profilePicturesFolder.path
        )
    }
}