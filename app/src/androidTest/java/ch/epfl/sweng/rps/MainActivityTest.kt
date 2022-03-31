package ch.epfl.sweng.rps


import android.view.Gravity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
class MainActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun checkFirstFragment(){
        onView(withId(R.id.fragment_home)).check(matches(isDisplayed()))
    }
    @Test
    fun checkLeaderboard(){
        onView(withId(R.id.nav_leaderboard)).perform(click())
        onView(withId(R.id.fragment_leaderboard)).check(matches(isDisplayed()))
    }
    @Test
    fun checkStatistics(){
        onView(withId(R.id.nav_statistics)).perform(click())
        onView(withId(R.id.fragment_statistics)).check(matches(isDisplayed()))
    }
    @Test
    fun checkBackHome(){
        onView(withId(R.id.nav_statistics)).perform(click())
        onView(withId(R.id.nav_home)).perform(click())
        onView(withId(R.id.fragment_home)).check(matches(isDisplayed()))
    }
}