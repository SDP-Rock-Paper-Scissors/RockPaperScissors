package ch.epfl.sweng.rps

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.db.FirebaseRepository
import ch.epfl.sweng.rps.models.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matcher
import org.hamcrest.core.IsInstanceOf.any
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class FirebaseTests {
    private val db = FirebaseRepository(Env.DEV)

    @Before
    fun setUp() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @After
    fun tearDown() {
        runBlocking {
            db.clearDevEnv()
        }
        Log.d("FirebaseTests", "tearDown done")
    }

    @get:Rule
    val thrown: ExpectedException = ExpectedException.none()


    private val isAnException: Matcher<Exception> = any(Exception::class.java)

    @Test
    fun testThrowsWhenNotLoggedIn() = runTest(UnconfinedTestDispatcher()) {
        FirebaseAuth.getInstance().signOut()

        assertEquals(false, db.isLoggedIn)
        thrown.expect(Exception::class.java)
        db.getCurrentUid();

        thrown.expect(isAnException)
        db.updateUser(User.Field.USERNAME to "test")

        thrown.expect(Exception::class.java)
        db.createUser("user1", "test@example.com")

        thrown.expect(Exception::class.java)
        db.sendFriendRequestTo("user1")

        thrown.expect(Exception::class.java)
        db.listFriendRequests()

        thrown.expect(Exception::class.java)
        db.acceptFriendRequest("user1")
    }
}
