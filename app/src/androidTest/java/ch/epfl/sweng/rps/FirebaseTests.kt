package ch.epfl.sweng.rps

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.db.FirebaseReferences
import ch.epfl.sweng.rps.db.FirebaseRepository
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.services.FirebaseGameService
import ch.epfl.sweng.rps.services.GameService
import ch.epfl.sweng.rps.services.ServiceLocator
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
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
        FirebaseAuth.getInstance().signInAnonymously()
    }

    @After
    fun tearDown() {
        Log.d("FirebaseTests", "tearDown done")
    }

    @Test
    fun testThrowsWhenNotLoggedIn() = runTest(UnconfinedTestDispatcher()) {
        FirebaseAuth.getInstance().signOut()

        assertEquals(false, db.isLoggedIn)
        assertThrows(Exception::class.java) {
            db.getCurrentUid()
        }

        assertThrows(Exception::class.java) {
            runBlocking {
                db.updateUser(User.Field.USERNAME to "test")
            }
        }

        assertThrows(Exception::class.java) {
            runBlocking {
                db.createUser("user1", "test@example.com")
            }
        }

        assertThrows(Exception::class.java) {
            runBlocking {
                db.sendFriendRequestTo("user1")
            }
        }

        assertThrows(Exception::class.java) {
            runBlocking {
                db.listFriendRequests()
            }
        }

        assertThrows(Exception::class.java) {
            runBlocking {
                db.acceptFriendRequestFrom("user1")
            }
        }
    }

    @Test
    fun testServiceLocator() {
        val serviceLocatorProd = ServiceLocator.getInstance(env = Env.Prod)
        assertEquals(Env.Prod, serviceLocatorProd.currentEnv())

        val serviceLocatorDev = ServiceLocator.getInstance(env = Env.Dev)
        assertEquals(Env.Dev, serviceLocatorDev.currentEnv())

        assertEquals(Env.Dev, serviceLocatorDev.getFirebaseReferences().env)
        assertEquals(Env.Prod, serviceLocatorProd.getFirebaseReferences().env)

        assertEquals(
            serviceLocatorProd.getFirebaseRepository(),
            serviceLocatorProd.getFirebaseRepository()
        )
    }

    @Test
    fun testGameService() {
        val serviceLocatorProd = ServiceLocator.getInstance(env = Env.Prod)
        assertEquals("1234", serviceLocatorProd.getGameServiceForGame("1234", start = false).gameId)
        assertTrue(
            serviceLocatorProd.getGameServiceForGame(
                "1",
                start = false
            ) === serviceLocatorProd.getGameServiceForGame("1", start = false)
        )

        val service = serviceLocatorProd.getGameServiceForGame("1234", start = false)
        assertFalse(service.ready)

        serviceLocatorProd.getGameServiceForGame("1234", start = true)
        assertTrue(service.active)

        val throwingActions: List<suspend (service: FirebaseGameService) -> Unit> =
            listOf(
                { it.startListening() }, // We already listened
                { it.refreshGame() },
                { it.addRound() },
                { it.playHand(Hand.PAPER) },
            )

        for (action in throwingActions) {
            assertThrows(Exception::class.java) {
                runBlocking {
                    action(service)
                }
            }
        }

        service.dispose()

        assertTrue(service.isDisposed)
    }

    @Test
    fun disposingGameServices() {
        val serviceLocator = ServiceLocator.getInstance(env = Env.Prod)
        val service = serviceLocator.getGameServiceForGame("1234", start = false)
        assertEquals(listOf("1234"), serviceLocator.cachedGameServices)
        assertFalse(service.isDisposed)
        serviceLocator.disposeAllGameServices()
        assertTrue(service.isDisposed)
        assertEquals(emptyList<String>(), serviceLocator.cachedGameServices)
    }

    @Test
    fun disposingGameServices2() {
        val serviceLocator = ServiceLocator.getInstance(env = Env.Prod)
        val service = serviceLocator.getGameServiceForGame("1234", start = false)
        assertEquals(listOf("1234"), serviceLocator.cachedGameServices)
        assertFalse(service.isDisposed)
        service.dispose()
        assertTrue(service.isDisposed)
        serviceLocator.disposeAllGameServices()
        assertTrue(service.isDisposed)
        assertEquals(emptyList<String>(), serviceLocator.cachedGameServices)
    }
}
