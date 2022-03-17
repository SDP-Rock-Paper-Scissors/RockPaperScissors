package ch.epfl.sweng.rps

import android.view.Gravity
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import ch.epfl.sweng.rps.ui.statistics.StatisticsFragment
import org.junit.Rule
import org.junit.Test

class StatisticsFragmentTest {
    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun clickOnStatistics_OpensStatisticFragment() {
        // Open Drawer to click on navigation.
        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.LEFT))) // Left Drawer should be closed.
            .perform(DrawerActions.open()) // Open Drawer

        // Start the screen of your activity.
        Espresso.onView(ViewMatchers.withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_statistics))

        // Check that you Activity was opened.
        Espresso.onView(ViewMatchers.withId(R.id.fragment_statistics))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))





    }
}