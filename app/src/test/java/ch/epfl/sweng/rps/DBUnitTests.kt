package ch.epfl.sweng.rps

import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.db.MockDatabase
import ch.epfl.sweng.rps.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class DBUnitTests {

    @Before
    fun setup() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)

        val firestore = FirebaseFirestore.getInstance()

        firestore.useEmulator("10.0.2.2", 8080)
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
    }

    fun <T> Task<T>.await(timeOut: Long = 30, timeUnit: TimeUnit = TimeUnit.SECONDS): T {
        val countDownLatch = CountDownLatch(1)
        continueWith {
            countDownLatch.countDown()
        }
        countDownLatch.await(timeOut, timeUnit)
        if (isSuccessful) {
            return result!!
        } else {
            throw exception ?: Exception("Unknown exception")
        }
    }


    @Test
    fun userManipulation() {
        val currentUid = "this_is_current_uid"
        val signal = CountDownLatch(1)

        val db = MockDatabase(currentUid)

        assertEquals(db.getCurrentUid(), currentUid)

        val name = "Jean Dupont"
        val email = "jean.dupont@gmail.com"
        db.createUser(name, email).continueWithTask {
            db.getUser(currentUid)
        }.continueWithTask { task ->
            val user = task.result!!
            assertEquals(email, user.email)
            assertEquals(name, user.username)
            assertEquals(currentUid, user.uid)

            db.updateUser(
                User.Field.gamesHistoryPublic() to true,
                User.Field.username() to "Gaëtan Schwartz"
            )
        }.continueWithTask {
            db.getUser(currentUid)
        }.continueWithTask { task ->
            val user = task.result!!
            assertEquals(true, user.gamesHistoryPublic)
            assertEquals("Gaëtan Schwartz", user.username)
            db.updateUser(
                User.Field.gamesHistoryPublic() to false,
                User.Field.username() to "John Appleseed"
            )
        }.continueWithTask {
            db.getUser(currentUid)
        }.continueWith { task ->
            val user = task.result!!
            assertEquals(false, user.gamesHistoryPublic)
            assertEquals("John Appleseed---", user.username)
            signal.countDown()
        }
        signal.await(60, TimeUnit.SECONDS)
    }
}

