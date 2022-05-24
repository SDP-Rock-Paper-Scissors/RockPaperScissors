package ch.epfl.sweng.rps.ui.friends

import android.view.View
import android.widget.ImageButton
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import ch.epfl.sweng.rps.ActivityScenarioRuleWithSetup
import ch.epfl.sweng.rps.ActivityScenarioRuleWithSetup.Companion.defaultTestFlow
import ch.epfl.sweng.rps.MainActivity
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.TestFlow
import ch.epfl.sweng.rps.models.ui.FakeFriendsData
import ch.epfl.sweng.rps.remote.Env
import ch.epfl.sweng.rps.remote.LocalRepository
import ch.epfl.sweng.rps.services.ServiceLocator
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class FriendsFragmentTest {

    val LIST_ITEM = FakeFriendsData.myFriendsData.size - 1
    val thisFriend = FakeFriendsData.myFriendsData[LIST_ITEM]

    @get:Rule
    val activityRule = ActivityScenarioRuleWithSetup(MainActivity::class.java,
        defaultTestFlow then TestFlow.onlySetup { ServiceLocator.localRepository.setCurrentUid("test") }
    )

    @Before
    fun setUp() {
        ServiceLocator.setCurrentEnv(Env.Test)
        val repo = ServiceLocator.getInstance().repository as LocalRepository
        repo.setCurrentUid("test")
    }

    @After
    fun tearDown() {
        val repo = ServiceLocator.getInstance().repository as LocalRepository
        repo.setCurrentUid(null)
        ServiceLocator.setCurrentEnv(Env.Prod)
    }

    @Test
    fun checkFriendsFragment() {
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
            .perform(
                actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(
                    LIST_ITEM,
                    ClickButtonAction.clickInfoButton(R.id.infoButton)
                )
            )

        onView(withId(R.id.fragment_info_page)).check(matches(isDisplayed()))
    }

    @Test
    fun test_GameFragmentShown_onPlayButtonClick() {
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(
                actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(
                    LIST_ITEM,
                    ClickButtonAction.clickPlayButton(R.id.playButton)
                )
            )

        onView(withId(R.id.fragment_game)).check(matches(isDisplayed()))
    }

    @Test
    fun test_CorrectGamesPlayedShows_onInfoButtonClick() {
        val gamesPlayed = thisFriend.gamesPlayed
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(
                actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(
                    LIST_ITEM,
                    ClickButtonAction.clickInfoButton(R.id.infoButton)
                )
            )

        onView(withId(R.id.gamesPlayedText_infoPage)).check(matches(withText("Games Played: $gamesPlayed")))
    }

    @Test
    fun test_CorrectGamesWonShows_onInfoButtonClick() {
        val gamesWon = thisFriend.gamesWon
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(
                actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(
                    LIST_ITEM,
                    ClickButtonAction.clickInfoButton(R.id.infoButton)
                )
            )

        onView(withId(R.id.gamesWonText_infoPage)).check(matches(withText("Games Won: $gamesWon")))
    }

    @Test
    fun test_CorrectUserNameShows_onInfoButtonClick() {

        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(
                actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(
                    LIST_ITEM,
                    ClickButtonAction.clickInfoButton(R.id.infoButton)
                )
            )

        onView(withId(R.id.userName_infoPage)).check(matches(withText(thisFriend.username)))
    }

    @Test
    fun test_CorrectWinRateShows_onInfoButtonClick() {
        val winRate = thisFriend.winRate
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(
                actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(
                    LIST_ITEM,
                    ClickButtonAction.clickInfoButton(R.id.infoButton)
                )
            )

        onView(withId(R.id.winRateText_infoPage)).check(matches(withText("Win Rate: $winRate%")))
    }

    @Test
    fun test_offlineStatusShows_onInfoButtonClick() {
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(
                actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(
                    LIST_ITEM,
                    ClickButtonAction.clickInfoButton(R.id.infoButton)
                )
            )

        onView(withId(R.id.offlineImage_infoPage)).check(matches(isDisplayed()))
    }

    @Test
    fun test_onlineStatusShows_onInfoButtonClick() {
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(
                actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(
                    0,
                    ClickButtonAction.clickInfoButton(R.id.infoButton)
                )
            )

        onView(withId(R.id.onlineImage_infoPage)).check(matches(isDisplayed()))
    }

    @Test
    fun test_returnsToFriendFragment_onBackButtonClick() {
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(
                actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(
                    LIST_ITEM,
                    ClickButtonAction.clickInfoButton(R.id.infoButton)
                )
            )

        onView(withId(R.id.infoPage_backButton)).perform(click())

        onView(withId(R.id.fragment_friends)).check(matches(isDisplayed()))
    }

    @Test
    fun test_goesToGameFragment_onPlayButtonClick() {
        onView(withId(R.id.nav_friends)).perform(click())

        onView(withId(R.id.friendListRecyclerView))
            .perform(
                actionOnItemAtPosition<FriendListAdapter.CardViewHolder>(
                    LIST_ITEM,
                    ClickButtonAction.clickInfoButton(R.id.infoButton)
                )
            )

        onView(withId(R.id.infoPage_playButton)).perform(click())

        onView(withId(R.id.fragment_game)).check(matches(isDisplayed()))
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