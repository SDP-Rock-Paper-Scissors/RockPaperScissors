package ch.epfl.sweng.rps

import android.content.Intent
import android.view.View
import android.widget.ImageButton
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.TestUtils.initializeForTest
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.models.FriendRequest
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.remote.LocalRepository
import ch.epfl.sweng.rps.services.ServiceLocator
import ch.epfl.sweng.rps.ui.friends.EspressoIdlingResource
import com.google.firebase.Timestamp
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


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
        repo.games.clear()


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
                FriendRequest(listOf("player3","player1"), Timestamp.now(),FriendRequest.Status.PENDING, "player3"),
                FriendRequest(listOf("player4","player1"), Timestamp.now(), FriendRequest.Status.ACCEPTED,"player4")
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
            .perform(actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(0,
                ClickButtonAction.clickInfoButton(R.id.infoButton)
            ))

        onView(withId(R.id.userName_infoPage)).check(matches(withText("player2")))
        onView(withId(R.id.gamesPlayedText_infoPage)).check(matches(withText("Games Played: $gamesPlayed")))
        onView(withId(R.id.gamesWonText_infoPage)).check(matches(withText("Games Won: $gamesWon")))
        onView(withId(R.id.winRateText_infoPage)).check(matches(withText("Win Rate: $winRate%")))
        onView(withId(R.id.onlineImage_infoPage)).check(matches(isDisplayed()))

    }

    /*@Test
    fun test_GameFragmentShown_onPlayButtonClick() {
        onView(withId(R.id.nav_friends)).perform(click())
        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(0,ClickButtonAction.clickPlayButton(R.id.playButton)))
        onView(withId(R.id.fragment_game)).check(matches(isDisplayed()))
    } */

    @Test
    fun test_goesToGameFragment_onPlayButtonClick(){
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(0,
                ClickButtonAction.clickInfoButton(R.id.infoButton)
            ))

        onView(withId(R.id.infoPage_playButton)).perform(click())

        onView(withId(R.id.fragment_game)).check(matches(isDisplayed()))
    }

    @Test
    fun test_returnsToFriendFragment_onBackButtonClick(){
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(0,
                ClickButtonAction.clickInfoButton(R.id.infoButton)
            ))

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

 /*   @Test
    fun test_FriendAdded_AfterAccepting(){
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.requestButton)).perform(click())
        onView(withId(R.id.myFriendReqButton)).perform(click())
        onView(withId(R.id.myReqsRecyclerView))
            .perform(actionOnItemAtPosition<RequestListAdapter.CardViewHolder>(0,
                ClickButtonAction.clickInfoButton(R.id.acceptButton)
            ))

        Espresso.pressBack()

        onView(withId(R.id.friendListRecyclerView))
            .perform(actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(3,
                ClickButtonAction.clickInfoButton(R.id.infoButton)
            ))

        onView(withId(R.id.userName_infoPage)).check(matches(withText("player3")))
    } */



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