package ch.epfl.sweng.rps

import android.content.Intent
import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test

class LoadingActivityTest {

    private fun createIntent(): Intent {
        val bundle = Bundle()
        bundle.putBoolean("isTest", true)
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            LoadingActivity::class.java
        )
        intent.putExtras(bundle)
        return intent
    }

    @get:Rule
    val rule =
        ActivityScenarioRule<LoginActivity>(createIntent())

    @Test
    fun start() {
        onView(withId(R.id.loadingActivityProgressBar)).check(matches(isDisplayed()))
    }
}