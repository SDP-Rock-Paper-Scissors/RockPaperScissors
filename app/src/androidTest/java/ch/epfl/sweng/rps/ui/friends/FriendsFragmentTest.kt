package ch.epfl.sweng.rps.ui.friends

import android.content.Intent
import android.service.autofill.Validators.not
import android.view.View
import android.widget.ImageButton
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.*
import ch.epfl.sweng.rps.TestUtils.initializeForTest
import ch.epfl.sweng.rps.remote.Env
import ch.epfl.sweng.rps.models.remote.FriendRequest
import ch.epfl.sweng.rps.models.remote.User
import com.google.firebase.Timestamp
import ch.epfl.sweng.rps.ActivityScenarioRuleWithSetup
import ch.epfl.sweng.rps.MainActivity
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.remote.LocalRepository
import ch.epfl.sweng.rps.services.ServiceLocator
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertIsNot


class FriendsFragmentTest {

   private fun createIntent(): Intent {
       Firebase.initializeForTest()
       val i: Intent = Intent(
           InstrumentationRegistry.getInstrumentation().targetContext,
           MainActivity::class.java
       )
       return i
   }
    @get:Rule
    val testRule = ActivityScenarioRuleWithSetup.default<MainActivity>(createIntent())

    @Before
    fun setUp() {
        ServiceLocator.setCurrentEnv(Env.Test)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        val repo = ServiceLocator.getInstance().repository as LocalRepository

        repo.setCurrentUid("player1")
        repo.users.clear()
        repo.friendRequests.clear()
        repo.userGames.clear()


        repo.users["player1"] = User(
            "player1",
            "player1",
            "public",
            true,
            "p1@example.com"
        )
        repo.users["player2"] = User(
            "player2",
            "player2",
            "public",
            true,
            "p2@example.com"
        )
        repo.users["player3"] = User(
            "player3",
            "player3",
            "public",
            true,
            "p3@example.com"
        )
        repo.users["player4"] = User(
            "player4",
            "player4",
            "public",
            true,
            "p4@example.com"
        )

        val friends: List<FriendRequest> = listOf(
                FriendRequest(listOf("player2","player1"), Timestamp.now() , FriendRequest.Status.ACCEPTED, "player2"),
                FriendRequest(listOf("player3","player1"), Timestamp.now(), FriendRequest.Status.PENDING, "player3"),
                FriendRequest(listOf("player1","player4"), Timestamp.now(), FriendRequest.Status.ACCEPTED,"player4")
        )

        repo.friendRequests.addAll(friends)
    }

    @After
    fun tearDown() {
        ServiceLocator.setCurrentEnv(Env.Prod)
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testEnv() {
        assert(ServiceLocator.getCurrentEnv() == Env.Test)
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
    fun test_CheckFriendInfo_onInfoButtonClick() {
        val gamesPlayed = 0
        val gamesWon = 0
        val winRate ="0.0"

        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(0,ClickButtonAction.clickInfoButton(R.id.infoButton)))

        onView(withId(R.id.userName_infoPage)).check(matches(withText("player2")))
        onView(withId(R.id.gamesPlayedText_infoPage)).check(matches(withText("Games Played: $gamesPlayed")))
        onView(withId(R.id.gamesWonText_infoPage)).check(matches(withText("Games Won: $gamesWon")))
        onView(withId(R.id.winRateText_infoPage)).check(matches(withText("Win Rate: $winRate%")))

    }

    @Test
    fun test_goesToGameFragment_onChoosing1GameMode(){
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(0,ClickButtonAction.clickPlayButton(R.id.playButton)))

        onView(withId(R.id.oneGameRadioBtn)).inRoot(isDialog()).perform(click())
        onView(withId(R.id.confirmButton)).inRoot(isDialog()).perform(click())

        onView(withId(R.id.matchmaking_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun test_goesToGameFragment_onChoosing5GameMode(){
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(0,ClickButtonAction.clickPlayButton(R.id.playButton)))

        onView(withId(R.id.fiveGameRadioBtn)).inRoot(isDialog()).perform(click())
        onView(withId(R.id.confirmButton)).inRoot(isDialog()).perform(click())

        onView(withId(R.id.matchmaking_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun test_dialogFragmentCloses_onClickingCancel(){
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(0,ClickButtonAction.clickPlayButton(R.id.playButton)))

        onView(withId(R.id.cancelButton)).inRoot(isDialog()).perform(click())

        onView(withId(R.id.fragment_game_mode_dialog)).check(doesNotExist())
    }

    @Test
    fun test_nothingHappens_onClickingSubmitWithoutSelection(){
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(0,ClickButtonAction.clickPlayButton(R.id.playButton)))

        onView(withId(R.id.confirmButton)).inRoot(isDialog()).perform(click())

        onView(withId(R.id.fragment_game_mode_dialog)).check(matches(isDisplayed()))

    }


    @Test
    fun test_returnsToFriendFragment_onBackButtonClick(){
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(0,ClickButtonAction.clickInfoButton(R.id.infoButton)))

        onView(withId(R.id.infoPage_backButton)).perform(click())

        onView(withId(R.id.fragment_friends)).check(matches(isDisplayed()))
    }

    @Test
    fun test_showsMyFriendReqs_onMyRequestButtonClicked(){
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.requestButton)).perform(click())
        onView(withId(R.id.myFriendReqButton)).perform(click())

        onView(withId(R.id.myReqsRecyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun test_showsAddFriendFragment_onAddFriendButtonClicked(){
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.requestButton)).perform(click())
        onView(withId(R.id.addFriendsButton)).perform(click())

        onView(withId(R.id.addFriendFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun test_FriendAdded_onAcceptedRequest(){
        onView(withId(R.id.nav_friends)).perform(click())
        onView(withId(R.id.requestButton)).perform(click())
        onView(withId(R.id.myFriendReqButton)).perform(click())

        onView(withId(R.id.myReqsRecyclerView))
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(0,ClickButtonAction.clickInfoButton(R.id.acceptButton)))

        pressBack()

        onView(withId(R.id.friendListRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(1,ClickButtonAction.clickInfoButton(R.id.infoButton)))

        onView(withId(R.id.userName_infoPage)).check(matches(withText("player3")))
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