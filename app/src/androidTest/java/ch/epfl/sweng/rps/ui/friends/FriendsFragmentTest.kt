package ch.epfl.sweng.rps.ui.friends

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.RequestListAdapter
import ch.epfl.sweng.rps.MainActivity
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.TestUtils.initializeForTest
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.models.FakeFriendsData
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.services.ServiceLocator
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before

import org.junit.Rule
import org.junit.Test


class FriendsFragmentTest {

    val LIST_ITEM = FakeFriendsData.myFriendsData.size - 1
    val thisFriend = FakeFriendsData.myFriendsData[LIST_ITEM]


   private fun createIntent(): Intent {
       Firebase.initializeForTest()
       val i: Intent = Intent(
           InstrumentationRegistry.getInstrumentation().targetContext,
           MainActivity::class.java
       )
       return i
   }
    @get:Rule
    val testRule = ActivityScenarioRule<MainActivity>(createIntent())

    @Before
    fun setUp() {
        ServiceLocator.setCurrentEnv(Env.Prod)

    }
    @After
    fun tearDown() {
        ServiceLocator.setCurrentEnv(Env.Prod)
    }

    @Test
    fun testEnv() {
        assert(ServiceLocator.getCurrentEnv() == Env.Prod)
    }

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
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(0,ClickButtonAction.clickInfoButton(R.id.infoButton)))

        onView(withId(R.id.fragment_info_page)).check(matches(isDisplayed()))
    }

    @Test
    fun test_GameFragmentShown_onPlayButtonClick() {
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(0,ClickButtonAction.clickPlayButton(R.id.playButton)))

        onView(withId(R.id.fragment_game)).check(matches(isDisplayed()))
    }

    @Test
    fun test_CorrectGamesPlayedShows_onInfoButtonClick(){
        val gamesPlayed = thisFriend.gamesPlayed
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(0,ClickButtonAction.clickInfoButton(R.id.infoButton)))

        onView(withId(R.id.gamesPlayedText_infoPage)).check(matches(withText("Games Played: $gamesPlayed")))
    }
    @Test
    fun test_CorrectGamesWonShows_onInfoButtonClick(){
        val gamesWon = thisFriend.gamesWon
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(0,ClickButtonAction.clickInfoButton(R.id.infoButton)))

        onView(withId(R.id.gamesWonText_infoPage)).check(matches(withText("Games Won: $gamesWon")))
    }

    @Test
    fun test_CorrectUserNameShows_onInfoButtonClick(){

        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(0,ClickButtonAction.clickInfoButton(R.id.infoButton)))

        onView(withId(R.id.userName_infoPage)).check(matches(withText(thisFriend.username)))
    }

    @Test
    fun test_CorrectWinRateShows_onInfoButtonClick(){
        val winRate = thisFriend.winRate
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(0,ClickButtonAction.clickInfoButton(R.id.infoButton)))

        onView(withId(R.id.winRateText_infoPage)).check(matches(withText("Win Rate: $winRate%")))
    }

  /*  @Test
    fun test_offlineStatusShows_onInfoButtonClick(){
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(LIST_ITEM,ClickButtonAction.clickInfoButton(R.id.infoButton)))

        onView(withId(R.id.offlineImage_infoPage)).check(matches(isDisplayed()))
    }
 */
    @Test
    fun test_onlineStatusShows_onInfoButtonClick(){
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(0,ClickButtonAction.clickInfoButton(R.id.infoButton)))

        onView(withId(R.id.onlineImage_infoPage)).check(matches(isDisplayed()))
    }

    @Test
    fun test_returnsToFriendFragment_onBackButtonClick(){
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(0,ClickButtonAction.clickInfoButton(R.id.infoButton)))

        onView(withId(R.id.infoPage_backButton)).perform(click())

        onView(withId(R.id.fragment_friends)).check(matches(isDisplayed()))
    }

    @Test
    fun test_goesToGameFragment_onPlayButtonClick(){
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(0,ClickButtonAction.clickInfoButton(R.id.infoButton)))

        onView(withId(R.id.infoPage_playButton)).perform(click())

        onView(withId(R.id.fragment_game)).check(matches(isDisplayed()))
    }
    @Test
    fun test_goesToRequestFragment_onRequestButtonClicked(){
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.requestButton)).perform(click())
        onView(withId(R.id.requestFragment)).check(matches(isDisplayed()))
    }
    @Test
    fun test_showsMyRequestFragment_onMyRequestButtonClicked(){
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.requestButton)).perform(click())
        onView(withId(R.id.myFriendReqButton)).perform(click())

        onView(withId(R.id.myFriendRequestsFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun test_showsAddFriendFragment_onAddFriendButtonClicked(){
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.requestButton)).perform(click())
        onView(withId(R.id.addFriendsButton)).perform(click())

        onView(withId(R.id.addFriendFragment)).check(matches(isDisplayed()))
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