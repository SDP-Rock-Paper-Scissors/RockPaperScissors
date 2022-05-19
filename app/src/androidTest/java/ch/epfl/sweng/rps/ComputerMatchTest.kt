package ch.epfl.sweng.rps

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.db.LocalRepository
import ch.epfl.sweng.rps.services.ServiceLocator
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ComputerMatchTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

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

    private suspend fun run1roundGame() {
        val radioButtonId = R.id.scissorsRB
        Espresso.onView(ViewMatchers.withId(R.id.button_play_1_games_offline))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(radioButtonId)).perform(ViewActions.click())
        delay(2_000)


    }

    @Test
    fun homeButtonExistAfterGame(): Unit = runBlocking {
        launch {
            run1roundGame()
        }.join()
        Espresso.onView(ViewMatchers.withId(R.id.backHomeButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun navigateHomeWorksAfterGame(): Unit = runBlocking {
        launch {
            run1roundGame()
        }.join()
        Espresso.onView(ViewMatchers.withId(R.id.backHomeButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.fragment_home))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}