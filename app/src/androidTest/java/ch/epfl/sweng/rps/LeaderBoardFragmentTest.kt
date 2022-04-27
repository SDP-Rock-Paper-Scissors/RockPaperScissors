package ch.epfl.sweng.rps

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.db.LocalRepository
import ch.epfl.sweng.rps.models.*
import ch.epfl.sweng.rps.services.ServiceLocator
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test



class LeaderBoardFragmentTest {
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
            TotalScore("player1",100),
            TotalScore("player2",200),
            TotalScore("player3",300))

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
        onView(withId(R.id.nav_leaderboard)).perform(click())

        // Check that you Activity was opened.
        onView(withId(R.id.fragment_leaderboard)).check(matches(isDisplayed()))
        onView(withText("player2")).check(matches(isDisplayed()))

    }

}

