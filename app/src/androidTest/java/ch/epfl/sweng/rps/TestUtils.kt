package ch.epfl.sweng.rps

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.remote.Env
import ch.epfl.sweng.rps.services.ServiceLocator
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matcher
import org.junit.rules.ExternalResource
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

    fun Firebase.initializeForTest() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
        FirebaseFirestore.getInstance().firestoreSettings =
            FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
    }
}


interface TestFlow {
    fun setup()
    fun tearDown()

    companion object {
        fun onlySetup(setup: () -> Unit) = object : TestFlow {
            override fun setup() = setup()

            override fun tearDown() {}
        }


        fun sameSetupAndTearDown(fn: () -> Unit) = object : TestFlow {
            override fun setup() = fn()

            override fun tearDown() = fn()
        }

        fun setupAndTearDown(setup: () -> Unit, tearDown: () -> Unit) = object : TestFlow {
            override fun setup() = setup()

            override fun tearDown() = tearDown()
        }

        val empty = object : TestFlow {
            override fun setup() {}

            override fun tearDown() {}
        }

        fun with(vararg flows: TestFlow) = object : TestFlow {
            override fun setup() {
                flows.forEach { it.setup() }
            }

            override fun tearDown() {
                flows.reversed().forEach { it.tearDown() }
            }
        }
    }

    /**
     * Runs [other] after `this`. In [tearDown], the order is reversed.
     */
    infix fun then(other: TestFlow) = with(this, other)
}

class ActivityScenarioRuleWithSetup<A : Activity?> : ExternalResource {
    private val scenarioSupplier: () -> ActivityScenario<A>
    private val testFlow: TestFlow

    private var _scenario: ActivityScenario<A>? = null

    companion object {
        /**
         * Runs
         * ```
         * Firebase.initializeForTest()
         * ServiceLocator.setCurrentEnv(Env.Test)
         * ```
         */
        val defaultTestFlow = TestFlow.setupAndTearDown(
            {
                ServiceLocator.setCurrentEnv(Env.Test)
                FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
                FirebaseFirestore.getInstance().firestoreSettings =
                    FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
                Cache.initialize(InstrumentationRegistry.getInstrumentation().targetContext)
                ServiceLocator.localRepository.setCurrentUid("test_user")
                ServiceLocator.localRepository.users["test_user"] = User(
                    "test_user",
                    "Test User",
                    User.Privacy.PUBLIC.name,
                    email = "email@example.com",
                    has_profile_photo = true,
                )
                Intents.init()

            },
            {
                kotlin.runCatching {
                    Cache.getInstance().clear()
                }
                ServiceLocator.localRepository.apply {
                    users.clear()
                    gamesMap.clear()
                    invitations.clear()
                    leaderBoardScore.clear()
                }
                FirebaseApp.clearInstancesForTest()
                Intents.release()
            }
        )

        fun <A : Activity> default(clazz: Class<A>): ActivityScenarioRuleWithSetup<A> {
            return ActivityScenarioRuleWithSetup(
                clazz,
                defaultTestFlow
            )
        }

        fun <A : Activity> default(intent: Intent): ActivityScenarioRuleWithSetup<A> {
            return ActivityScenarioRuleWithSetup(
                intent,
                defaultTestFlow
            )
        }
    }

    /**
     * Constructs ActivityScenarioRule for a given activity class.
     *
     * @param activityClass an activity class to launch
     */
    constructor(activityClass: Class<A>, testFlow: TestFlow = TestFlow.empty) {
        scenarioSupplier = {
            ActivityScenario.launch(activityClass)
        }
        this.testFlow = testFlow
    }

    /**
     * @see .ActivityScenarioRule
     * @param activityOptions an activity options bundle to be passed along with the intent to start
     * activity.
     */
    constructor(
        activityClass: Class<A>,
        activityOptions: Bundle?,
        testFlow: TestFlow = TestFlow.empty
    ) {
        scenarioSupplier = {
            ActivityScenario.launch(
                activityClass,
                activityOptions
            )
        }
        this.testFlow = testFlow
    }

    /**
     * Constructs ActivityScenarioRule with a given intent.
     *
     * @param startActivityIntent an intent to start an activity
     */
    constructor(startActivityIntent: Intent, testFlow: TestFlow = TestFlow.empty) {
        scenarioSupplier =
            {
                ActivityScenario.launch(startActivityIntent)
            }
        this.testFlow = testFlow
    }


    /**
     * @see .ActivityScenarioRule
     * @param activityOptions an activity options bundle to be passed along with the intent to start
     * activity.
     */
    constructor(
        startActivityIntent: Intent,
        activityOptions: Bundle?,
        testFlow: TestFlow = TestFlow.empty
    ) {
        scenarioSupplier =
            {
                ActivityScenario.launch(
                    startActivityIntent,
                    activityOptions
                )
            }
        this.testFlow = testFlow
    }


    @Throws(Throwable::class)
    override fun before() {
        testFlow.setup()
        _scenario = scenarioSupplier.invoke()
    }

    override fun after() {
        _scenario!!.close()
        testFlow.tearDown()
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


