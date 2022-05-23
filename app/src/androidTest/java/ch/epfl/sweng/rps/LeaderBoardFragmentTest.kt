package ch.epfl.sweng.rps

import android.content.Intent
import android.os.Bundle
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.remote.Env
import ch.epfl.sweng.rps.remote.LocalRepository
import ch.epfl.sweng.rps.models.remote.TotalScore
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.services.ServiceLocator
import org.hamcrest.Matchers.anything
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class LeaderBoardFragmentTest {
    @get:Rule
    val testRule = ActivityScenarioRuleWithSetup.default<MainActivity>(
        createIntent()
    )

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
        //must call it for logged in!!
        repo.setCurrentUid("player1")
        repo.leaderBoardScore.clear()
        repo.users.clear()

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
        repo.users["player3"] = User(
            "player3",
            "player3",
            "public",
            true,
            "email@example.com"
        )

        repo.leaderBoardScore = mutableListOf(
            TotalScore("player1", 100, 350),
            TotalScore("player2", 200, 250),
            TotalScore("player3", 300, 150)
        )


    }


    @After
    fun tearDown() {
        ServiceLocator.setCurrentEnv(Env.Prod)
    }

    @Test
    fun testEnv() {
        assertEquals(Env.Test, ServiceLocator.getCurrentEnv())
    }


    @Test
    fun opensLeaderBoardFragmentTest() {

        onView(withId(R.id.nav_leaderboard)).perform(click())
        onView(withId(R.id.fragment_leaderboard)).check(matches(isDisplayed()))
        onView(withText("player1")).check(matches(isDisplayed()))
        onView(withText("100")).check(matches(isDisplayed()))
        onView(withText("player2")).check(matches(isDisplayed()))
        onView(withText("200")).check(matches(isDisplayed()))
        onView(withText("player3")).check(matches(isDisplayed()))
        onView(withText("300")).check(matches(isDisplayed()))

    }

    @Test
    fun opensLeaderboardSpinnerFragmentTest() {
        onView(withId(R.id.nav_leaderboard)).perform(click())
        onView(withId(R.id.modeSelect_leaderboard)).perform(click())
        onData(anything()).atPosition(1).perform(click())
        onView(withText("100")).check(doesNotExist())
        onView(withText("150")).check(matches(isDisplayed()))
        onView(withId(R.id.modeSelect_leaderboard)).perform(click())
        onData(anything()).atPosition(0).perform(click())
        onView(withText("250")).check(doesNotExist())
        onView(withText("200")).check(matches(isDisplayed()))


    }

}

