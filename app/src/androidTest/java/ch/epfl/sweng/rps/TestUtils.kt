package ch.epfl.sweng.rps

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import org.hamcrest.Matcher
import java.util.concurrent.TimeoutException

object TestUtils {
    inline fun <reified T : Activity> getActivityInstance(): T {
        var activity: Activity? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val resumedActivities =
                ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
            Log.i("SettingsPageTest", "resumedActivities: $resumedActivities")
            if (resumedActivities.iterator().hasNext()) {
                activity = resumedActivities.iterator().next()
            }
        }
        if (activity == null) {
            throw TimeoutException("No activity found")
        }
        if (activity !is T) {
            throw IllegalStateException("Activity is not of type ${T::class.java.simpleName} but of type ${activity!!.javaClass.simpleName}")
        }
        return activity!! as T
    }

    fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = ViewMatchers.isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }

    fun Firebase.initializeForTest() {
        Firebase.initialize(InstrumentationRegistry.getInstrumentation().targetContext)
        Firebase.firestore.firestoreSettings =
            FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
    }

    fun retry(
        maxRetries: Int = 3,
        retryDelay: Long = 1000L,
        action: () -> Unit,
    ): Boolean {
        var retries = 0
        while (retries < maxRetries) {
            try {
                action()
                return true
            } catch (e: Exception) {
                retries++
                if (retries >= maxRetries) {
                    throw e
                }
                Thread.sleep(retryDelay)
            }
        }
        return false
    }

    fun retryPredicate(
        maxRetries: Int = 3,
        retryDelay: Long = 1000L,
        action: () -> Boolean,
    ): Boolean {
        var retries = 0
        while (retries < maxRetries) {
            try {
                if (action()) {
                    return true
                }
            } catch (e: Exception) {
                retries++
                if (retries >= maxRetries) {
                    throw e
                }
                Thread.sleep(retryDelay)
            }
        }
        return false
    }
}