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
import org.junit.Test
import kotlin.test.assertEquals


class OnBoardingActivityTest {
    private fun createIntent(): Intent {
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            OnBoardingActivity::class.java
        )
        intent.putExtra(
            OnBoardingActivity.AFTER_ONBOARDING_DONE_EXTRA,
            OnBoardingActivity.AfterOnboardingAction.FINISH
        )
        return intent
    }

    @Test
    fun onCreate() {
        val scenario = ActivityScenario.launch<OnBoardingActivity>(createIntent())
        onView(withId(R.id.onboarding_layout)).check(matches(isDisplayed()))
        val activity = TestUtils.getActivityInstance<OnBoardingActivity>()
        activity.navOut()
        TestUtils.retryPredicate(retryDelay = 200, maxRetries = 10) {
            scenario.state == Lifecycle.State.DESTROYED
        }
        assertEquals(Lifecycle.State.DESTROYED, scenario.state)
    }
}