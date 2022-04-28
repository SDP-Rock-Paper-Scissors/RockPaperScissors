package ch.epfl.sweng.rps


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.sweng.rps.TestUtils.initializeForTest
import com.google.firebase.ktx.Firebase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        Firebase.initializeForTest()
    }

    @Test
    fun checkFirstFragment() {
        onView(withId(R.id.fragment_home)).check(matches(isDisplayed()))
    }

    @Test
    fun checkLeaderboard() {
        onView(withId(R.id.nav_leaderboard)).perform(click())
        onView(withId(R.id.fragment_leaderboard)).check(matches(isDisplayed()))
    }

    @Test
    fun checkStatistics() {
        onView(withId(R.id.nav_statistics)).perform(click())
        onView(withId(R.id.fragment_statistics)).check(matches(isDisplayed()))
    }
}