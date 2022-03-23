package ch.epfl.sweng.rps


import android.view.Gravity
import androidx.test.espresso.Espresso.onView
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
    fun clickOnStatistics_OpensStatisticFragment() {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
            .perform(DrawerActions.open()) // Open Drawer

        // Start the screen of your activity.
        onView(withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_statistics))

        // Check that you Activity was opened.
        onView(withId(R.id.fragment_statistics)).check(matches(isDisplayed()))
    }

    @Test
    fun clickOnLeaderboard_OpensLeaderboardFragment() {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
            .perform(DrawerActions.open()) // Open Drawer

        // Start the screen of your activity.
        onView(withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_leaderboard))

        // Check that you Activity was opened.
        onView(withId(R.id.fragment_leaderboard)).check(matches(isDisplayed()))
    }

    /* Basic implementation for the test of the action bar menu. Can be implemented when something is connected to settings
    @Test
    fun openSettingsMenu() {
        // Click menu
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        // Choose item "Settings"
        onView(withId(R.id.action_settings)).perform(click());
    } */
}