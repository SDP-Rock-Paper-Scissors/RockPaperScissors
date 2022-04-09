package ch.epfl.sweng.rps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.db.LocalRepository
import ch.epfl.sweng.rps.models.Game
import ch.epfl.sweng.rps.models.Hand
import ch.epfl.sweng.rps.models.Round
import ch.epfl.sweng.rps.models.User
import ch.epfl.sweng.rps.services.ServiceLocator
import com.google.firebase.Timestamp
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeoutException


class StatisticsFragmentTest {
    @get:Rule
    val testRule = ActivityScenarioRule<MainActivity>(createIntent())

    private fun createIntent(): Intent {
        val i = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            MainActivity::class.java
        )
        val bundle = Bundle()
        bundle.putBoolean("test", true)
        i.putExtra("Context", bundle)
        return i
    }

    @Before
    fun setUp() {
        ServiceLocator.setCurrentEnv(Env.Test)
        val repo = ServiceLocator.getInstance().repository as LocalRepository
        repo.setCurrentUid("player1")
        repo.games.clear()
        repo.users.clear()
        repo.games["game1"] = Game(
            "game1",
            listOf("player1", "player2"),
            mapOf(
                "0" to Round(
                    mapOf(
                        "player1" to Hand.ROCK,
                        "player2" to Hand.SCISSORS
                    ),
                    Timestamp(Date(0))
                ),
                "1" to Round(
                    mapOf(
                        "player1" to Hand.SCISSORS,
                        "player2" to Hand.PAPER
                    ),
                    Timestamp(Date(0))
                )
            ),
            1,
            Game.GameMode(
                2,
                Game.GameMode.Type.PVP,
                3,
                10
            ).toGameModeString(),
            true,
            Timestamp(Date(0)),
            2
        )

        repo.users["player1"] = User(
            "player1",
            "player1",
            "public",
            true,
            "email@example.com"
        )
        repo.users["player2"] = User(
            "player2",
            "player2",
            "public",
            true,
            "email@example.com"
        )
    }


    @After
    fun tearDown() {
        ServiceLocator.setCurrentEnv(Env.Prod)
    }

    @Test
    fun testEnv() {
        assert(ServiceLocator.getCurrentEnv() == Env.Test)
    }

    @Test
    fun opensStatisticFragmentTest() {
        onView(withId(R.id.nav_statistics)).perform(click())

        // Check that you Activity was opened.
        onView(withId(R.id.fragment_statistics)).check(matches(isDisplayed()))
        onView(withText("player2")).check(matches(isDisplayed()))


        onView(withId(R.id.test_for_stats_row)).perform(click())

        onView(withId(R.id.fragment_match_details)).check(matches(isDisplayed()))
    }

    /** Perform action of waiting for a specific view id.  */
    fun waitId(viewId: Int, millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "wait for a specific view with id <$viewId> during $millis millis."
            }

            override fun perform(uiController: UiController, view: View?) {
                uiController.loopMainThreadUntilIdle()
                val startTime = System.currentTimeMillis()
                val endTime = startTime + millis
                val viewMatcher: Matcher<View> = withId(viewId)
                do {
                    for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return
                        }
                    }
                    uiController.loopMainThreadForAtLeast(50)
                } while (System.currentTimeMillis() < endTime)
                throw PerformException.Builder()
                    .withActionDescription(this.description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(TimeoutException())
                    .build()
            }
        }
    }

}

