package ch.epfl.sweng.rps

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.sweng.rps.remote.Env
import ch.epfl.sweng.rps.services.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class GameButtonsTest {

    @get:Rule
    val testRule = ActivityScenarioRuleWithSetup.default(MainActivity::class.java)

    @Before
    fun setUp() {
        ServiceLocator.localRepository.setCurrentUid("test")
    }

    @After
    fun tearDown() {
        ServiceLocator.setCurrentEnv(Env.Prod)
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

    private fun checkPressedButton(radioButtonId: Int) = runBlocking {
        onView(withId(R.id.button_play_1_games_offline)).perform(click())
        onView(withId(radioButtonId)).perform(click())
        delay(2_000)

        onView(withId(R.id.game_result_communicate)).check(matches(isDisplayed()))

    }
}

