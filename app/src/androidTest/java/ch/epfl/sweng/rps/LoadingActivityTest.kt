package ch.epfl.sweng.rps

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test

class LoadingActivityTest {

    private fun createIntent(): Intent {
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            LoadingActivity::class.java
        )
        intent.putExtra(LoadingActivity.IS_TEST_EXTRA, true)
        return intent
    }

    @get:Rule
    val rule = ActivityScenarioRuleWithSetup.default<LoadingActivity>(createIntent())

    @Test
    fun start() {
        onView(withId(R.id.loadingActivityProgressBar)).check(matches(isDisplayed()))
    }

    @Test
    fun testNav() {
        rule.scenario.onActivity {
            onView(withId(R.id.loadingActivityProgressBar)).check(matches(isDisplayed()))
            it.nav()
        }

    }
}