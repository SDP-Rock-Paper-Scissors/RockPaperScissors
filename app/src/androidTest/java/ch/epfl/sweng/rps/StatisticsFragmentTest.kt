package ch.epfl.sweng.rps

import android.view.Gravity

import androidx.test.espresso.Espresso

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test


class StatisticsFragmentTest {
    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun opensStatisticFragmentTest() {
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

        Espresso.onView(ViewMatchers.isClickable())

        Espresso.onView(withId(R.id.test_for_stats_row)).perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.fragment_match_details))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))


        }
    }

