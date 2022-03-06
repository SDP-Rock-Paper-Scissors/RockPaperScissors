package ch.epfl.sweng.rps

import android.os.Looper
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.db.FirestoreDatabase
import ch.epfl.sweng.rps.models.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class DBUnitTests {

    @Before
    fun setup() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)

        val firestore = FirebaseFirestore.getInstance()
        firestore.useEmulator("10.0.2.2", 8080)

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        firestore.firestoreSettings = settings
    }

    private fun <T> waitForTask(
        task: Task<T>,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        timeout: Long = 30
    ): T {
        val signal = CountDownLatch(1)
        thread {
            Tasks.await(task)
            signal.countDown()
        }
        signal.await()
        return task.result!!
    }

    @Test
    fun userManipulation() {
        val db = mock(FirestoreDatabase::class.java)

        val currentUid = "this_is_current_uid"

        `when`(db.getCurrentUid()).thenReturn(currentUid)

        assertEquals(db.getCurrentUid(), currentUid)

        val name = "Jean Dupont"
        val email = "jean.dupont@gmail.com"
        waitForTask(db.createUser(name, email))


        val userTask = db.getUser(currentUid)
        assertNotNull(userTask)
        val user = waitForTask(userTask)

        assertEquals(email, user.email)
        assertEquals(name, user.username)
        assertEquals(currentUid, user.uid)

        waitForTask(
            db.updateUser(
                User.Field.gamesHistoryPublic() to true,
                User.Field.username() to "Gaëtan Schwartz"
            )
        )

        assertEquals(true, user.gamesHistoryPublic)
        assertEquals("Gaëtan Schwartz", user.username)

        waitForTask(
            db.updateUser(
                User.Field.gamesHistoryPublic() to false,
                User.Field.username() to "John Appleseed"
            )
        )

        assertEquals(false, user.gamesHistoryPublic)
        assertEquals("John Appleseed", user.username)
    }

}