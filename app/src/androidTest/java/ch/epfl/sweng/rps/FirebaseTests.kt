package ch.epfl.sweng.rps

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.db.FirebaseRepository
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
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

    // @Test
    fun testCreateUser() {
        runTest {
            db.createUser("Gaëtan S.", "gaetan.schwartz@epfl.ch")
        }
    }
}