package ch.epfl.sweng.rps

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.db.FirebaseReferences
import ch.epfl.sweng.rps.db.FirebaseRepository
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.services.ServiceLocator
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matcher
import org.hamcrest.core.IsInstanceOf.any
import org.junit.*
import org.junit.Assert.assertEquals
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
    private val db = FirebaseRepository(FirebaseReferences(env = Env.Dev))

    @Before
    fun setUp() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @After
    fun tearDown() {
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
        db.acceptFriendRequestFrom("user1")
    }

    @Test
    fun testServiceLocator() {
        val serviceLocatorProd = ServiceLocator.getInstance(env = Env.Prod)
        assertEquals(Env.Prod, serviceLocatorProd.currentEnv())

        val serviceLocatorDev = ServiceLocator.getInstance(env = Env.Dev)
        assertEquals(Env.Dev, serviceLocatorDev.currentEnv())

        assertEquals(Env.Dev, serviceLocatorDev.getFirebaseReferences().env)
        assertEquals(Env.Prod, serviceLocatorProd.getFirebaseReferences().env)
    }

    @Test
    fun testGameService() {
        val serviceLocatorProd = ServiceLocator.getInstance(env = Env.Prod)
        assertEquals("1", serviceLocatorProd.getGameServiceForGame("1", listen = false).getGameId())
        Assert.assertTrue(
            serviceLocatorProd.getGameServiceForGame(
                "1",
                listen = false
            ) === serviceLocatorProd.getGameServiceForGame("1", listen = false)
        )
    }
}
