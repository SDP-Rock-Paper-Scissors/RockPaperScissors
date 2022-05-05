package ch.epfl.sweng.rps

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import ch.epfl.sweng.rps.TestUtils.initializeForTest
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.services.ServiceLocator
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import org.hamcrest.Matcher
import org.junit.rules.ExternalResource
import java.util.concurrent.TimeoutException

object TestUtils {
    inline fun <reified T : Activity> getActivityInstance(): T {
        var activity: Activity? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val resumedActivities: Collection<Activity> =
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
}

class ActivityScenarioRuleWithSetup<A : Activity?> : ExternalResource {

    private val scenarioSupplier: () -> ActivityScenario<A>
    private val setup: () -> Unit

    private var _scenario: ActivityScenario<A>? = null

    companion object {
        /**
         * Runs
         * ```
         * Firebase.initializeForTest()
         * ServiceLocator.setCurrentEnv(Env.Test)
         * ```
         */
        val defaultSetup = {
            Firebase.initializeForTest()
            ServiceLocator.setCurrentEnv(Env.Test)
        }

        fun <A : Activity> default(clazz: Class<A>): ActivityScenarioRuleWithSetup<A> {
            return ActivityScenarioRuleWithSetup(
                clazz,
                defaultSetup
            )
        }

        fun <A : Activity> default(intent: Intent): ActivityScenarioRuleWithSetup<A> {
            return ActivityScenarioRuleWithSetup(
                intent,
                defaultSetup
            )
        }
    }

    /**
     * Constructs ActivityScenarioRule for a given activity class.
     *
     * @param activityClass an activity class to launch
     */
    constructor(activityClass: Class<A>, setup: () -> Unit = {}) {
        scenarioSupplier = {
            ActivityScenario.launch(activityClass)
        }
        this.setup = setup
    }

    /**
     * @see .ActivityScenarioRule
     * @param activityOptions an activity options bundle to be passed along with the intent to start
     * activity.
     */
    constructor(activityClass: Class<A>, activityOptions: Bundle?, setup: () -> Unit = {}) {
        scenarioSupplier = {
            ActivityScenario.launch(
                activityClass,
                activityOptions
            )
        }
        this.setup = setup
    }

    /**
     * Constructs ActivityScenarioRule with a given intent.
     *
     * @param startActivityIntent an intent to start an activity
     */
    constructor(startActivityIntent: Intent, setup: () -> Unit = {}) {
        scenarioSupplier =
            {
                ActivityScenario.launch(startActivityIntent)
            }
        this.setup = setup
    }


    /**
     * @see .ActivityScenarioRule
     * @param activityOptions an activity options bundle to be passed along with the intent to start
     * activity.
     */
    constructor(startActivityIntent: Intent, activityOptions: Bundle?, setup: () -> Unit = {}) {
        scenarioSupplier =
            {
                ActivityScenario.launch(
                    startActivityIntent,
                    activityOptions
                )
            }
        this.setup = setup
    }


    @Throws(Throwable::class)
    override fun before() {
        setup.invoke()
        _scenario = scenarioSupplier.invoke()
    }

    override fun after() {
        _scenario!!.close()
    }

    /**
     * Returns [ActivityScenario] of the given activity class.
     *
     * @throws NullPointerException if you call this method while test is not running
     * @return a non-null [ActivityScenario] instance
     */
    val scenario: ActivityScenario<A>
        get() = _scenario!!
}
