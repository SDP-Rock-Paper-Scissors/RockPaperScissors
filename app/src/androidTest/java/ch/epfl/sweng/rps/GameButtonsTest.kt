package ch.epfl.sweng.rps

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GameButtonsTest {
    @get:Rule
    val testRule = ActivityTestRule(MainActivity::class.java)
    @Before
    fun launch(){
        testRule.launchActivity(Intent())
    }
    @Test
    fun pressedRock() {
        checkPressedButton(R.id.rockRB)
    }

    @Test
    fun pressedPaper() {
        checkPressedButton(R.id.paperRB)
    }

    @Test
    fun pressedScissors() {
        checkPressedButton(R.id.scissorsRB)
    }

    private fun checkPressedButton(radioButtonId: Int) {
        onView(withId(R.id.button_play_1_games_offline)).perform(click())
        onView(withId(radioButtonId)).perform(click())
        onView(withId(R.id.game_result_communicate)).check(matches(isDisplayed()))

    }
}

