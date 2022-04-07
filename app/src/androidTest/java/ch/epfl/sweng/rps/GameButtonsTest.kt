package ch.epfl.sweng.rps

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.anyOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GameButtonsTest {
    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

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

