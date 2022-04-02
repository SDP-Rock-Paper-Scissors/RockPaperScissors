package ch.epfl.sweng.rps.ui.friends

import android.view.View
import android.widget.ImageButton
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import ch.epfl.sweng.rps.FriendListAdapter
import ch.epfl.sweng.rps.MainActivity
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.models.FakeFriendsData
import org.hamcrest.Matcher

import org.junit.Rule
import org.junit.Test


class FriendsFragmentTest {

    val LIST_ITEM = FakeFriendsData.myFriendsData.size - 1
    val thisFriend = FakeFriendsData.myFriendsData[LIST_ITEM]

    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun checkFriendsFragment(){
        onView(withId(R.id.nav_friends)).perform(click())
        onView(withId(R.id.fragment_friends)).check(matches(isDisplayed()))
    }

    @Test
    fun test_isRecyclerViewVisible_onLaunch() {
        onView(withId(R.id.nav_friends)).perform(click())
        onView(withId(R.id.friendListRecyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun test_FriendInfoFragmentShown_onInfoButtonClick() {
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(LIST_ITEM,ClickButtonAction.clickInfoButton(R.id.infoButton)))

        onView(withId(R.id.infoPage_Fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun test_GameFragmentShown_onPlayButtonClick() {
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(LIST_ITEM,ClickButtonAction.clickPlayButton(R.id.playButton)))

        onView(withId(R.id.gameFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun test_CorrectUserInfoShows_onInfoButtonClick(){
        /* Check username,
         check online status image
         check stats
        */
    }

    @Test
    fun test_GameFragmentShown_fromInfoPage() {
        //Play button from InfoPage should lead to gameFragment upon clicking
    }

    @Test



    @Test
    fun onViewCreated() {
    }

    @Test
    fun onButtonClick() {
    }
}
class ClickButtonAction {
    companion object {
        fun clickInfoButton(childId: Int): ViewAction {
            return object : ViewAction {
                override fun getConstraints(): Matcher<View> {
                  return isAssignableFrom(ImageButton::class.java)
                }

                override fun getDescription(): String {
                    return "InfoButton Clicked"
                }

                override fun perform(uiController: UiController?, view: View?) {
                    val v = view?.findViewById<ImageButton>(childId)
                    v?.performClick()
                }

            }
        }

        fun clickPlayButton(childId: Int): ViewAction {
            return object : ViewAction {
                override fun getConstraints(): Matcher<View> {
                    return isAssignableFrom(ImageButton::class.java)
                }

                override fun getDescription(): String {
                    return "PlayButton Clicked"
                }

                override fun perform(uiController: UiController?, view: View?) {
                    val v = view?.findViewById<ImageButton>(childId)
                    v?.performClick()
                }

            }
        }
    }
}