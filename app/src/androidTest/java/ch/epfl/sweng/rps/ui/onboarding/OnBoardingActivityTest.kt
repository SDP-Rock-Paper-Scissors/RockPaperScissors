package ch.epfl.sweng.rps.ui.onboarding

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.TestUtils
import ch.epfl.sweng.rps.TestUtils.retryPredicate
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.assertEquals


class OnBoardingActivityTest {
    private fun createIntent(): Intent {
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            OnBoardingActivity::class.java
        )
        intent.putExtra(OnBoardingActivity.DESTINATION_EXTRA, OnBoardingActivity.Destination.FINISH)
        return intent
    }

    @Test
    fun onCreate() {
        val scenario = ActivityScenario.launch<OnBoardingActivity>(createIntent())
        onView(withId(R.id.onboarding_layout)).check(matches(isDisplayed()))
        val atomicBoolean = AtomicBoolean(false)
        val activity = TestUtils.getActivityInstance<OnBoardingActivity>()
        OnBoardingActivity.navOut(activity)
        retryPredicate(retryDelay = 200, maxRetries = 10) {
            scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(Lifecycle.State.DESTROYED, scenario.state)
    }
}