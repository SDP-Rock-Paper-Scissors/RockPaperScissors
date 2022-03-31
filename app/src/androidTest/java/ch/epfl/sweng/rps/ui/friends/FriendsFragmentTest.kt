package ch.epfl.sweng.rps.ui.friends

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.ActivityTestRule
import ch.epfl.sweng.rps.FriendListAdapter
import ch.epfl.sweng.rps.MainActivity
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.ToastMatcher
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule


class FriendsFragmentTest {

    @Rule
    @JvmField
    public var activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun test_isRecyclerViewVisible_onLaunch() {
        Espresso.onView(withId(R.id.friendListRecyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun test_showRightToastMessage_onPressInfoButton() {
        Espresso.onView(withId(R.id.friendListRecyclerView)).perform(actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(9, click()))

        Espresso.onView(withText("This is Insomnix's info"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))

    }
    @Test
    fun test_showRightToastMessage_onPressPlayButton() {
        Espresso.onView(withId(R.id.friendListRecyclerView)).perform(actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(8, click()))

        Espresso.onView(withText("You will play a game with Narut0"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))

    }

    @Test
    fun onViewCreated() {
    }

    @Test
    fun onButtonClick() {
    }
}