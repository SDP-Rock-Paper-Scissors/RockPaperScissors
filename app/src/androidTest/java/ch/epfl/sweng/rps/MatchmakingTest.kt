package ch.epfl.sweng.rps

import android.content.Intent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.TestUtils.waitForView
import ch.epfl.sweng.rps.models.remote.Game
import ch.epfl.sweng.rps.models.remote.GameMode
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.remote.Env
import ch.epfl.sweng.rps.services.FirebaseGameService
import ch.epfl.sweng.rps.services.MatchmakingService
import ch.epfl.sweng.rps.services.ServiceLocator
import ch.epfl.sweng.rps.services.TestServiceLocator
import ch.epfl.sweng.rps.ui.game.MatchmakingFragment
import ch.epfl.sweng.rps.utils.TEST_MODE
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class MatchmakingTest {

    data class Cfg(val currentGameId: String?, val gameMode: GameMode)

    private fun firebaseGameService(gameId: String, gameMode: GameMode): FirebaseGameService {
        val m = mockk<FirebaseGameService>()
        every { m.gameId } returns gameId
        every { m.opponentCount() } returns flow {
            for (i in 0 until gameMode.playerCount) {
                emit(FirebaseGameService.PlayerCount.Some(i))
                delay(50)
            }
            emit(FirebaseGameService.PlayerCount.Full(gameMode.playerCount))

        }
        val game = mockk<Game>()
        every { game.game_mode } returns gameMode.toGameModeString()
        every { game.gameMode } returns gameMode
        every { game.id } returns gameId
        every { m.currentGame } returns game
        coEvery { m.waitForGameStart() } answers {
            runBlocking {
                delay(50)
                true
            }
        }
        coEvery { m.refreshGame() } answers {
            runBlocking {
                delay(50)
                mockk()
            }
        }
        return m
    }

    @Test
    fun testMatchmakingWithExistingGame() {
        setup(Cfg("gameId", GameMode.default(2)))
        val scenario = ActivityScenario.launch<MainActivity>(Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            MainActivity::class.java,
        ).apply {
            putExtra(TEST_MODE, true)
        })

        scenario.use {
            onView(withId(R.id.button_play_1_games_online)).perform(click())
            onView(withId(R.id.matchmaking_fragment)).check(matches(isDisplayed()))
            onView(isRoot()).perform(waitForView(withText(R.string.ready_to_play), 10_000))
        }
    }

    @Test
    fun testMatchmakingWithoutExistingGame() {
        setup(Cfg(null, GameMode.default(2)))
        val scenario = ActivityScenario.launch<MainActivity>(Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            MainActivity::class.java,
        ).apply {
            putExtra(TEST_MODE, true)
        })

        scenario.use {
            onView(withId(R.id.button_play_1_games_online)).perform(click())
            onView(withId(R.id.matchmaking_fragment)).check(matches(isDisplayed()))
            onView(isRoot()).perform(waitForView(withText(R.string.ready_to_play), 10_000))
        }
    }

    private fun setup(cfg: Cfg) {
        ServiceLocator.setCurrentEnv(Env.Test)
        Cache.Companion.initialize(InstrumentationRegistry.getInstrumentation().targetContext)
        ServiceLocator.localRepository.setCurrentUid("user1")
        ServiceLocator.localRepository.users["user1"] = User(
            "user 1", "user1", User.Privacy.PUBLIC.name, email = "user1@example.com"
        )
        val mm = mockk<MatchmakingService>()
        coEvery { mm.currentGame() } answers {
            if (cfg.currentGameId != null) firebaseGameService(cfg.currentGameId, cfg.gameMode)
            else null
        }
        every { mm.queue(any()) } returns flow {
            delay(50)
            emit(MatchmakingService.QueueStatus.Queued(cfg.gameMode))
            delay(50)
            emit(MatchmakingService.QueueStatus.GameJoined(firebaseGameService("gameId", cfg.gameMode)))
        }
        val sl = ServiceLocator.getInstance() as TestServiceLocator
        sl.matchmakingService = mm
    }

    @Test
    fun testMatchmakingException(): Unit = runBlocking {
        val matchmakingTimeoutException = MatchmakingFragment.MatchmakingTimeoutException("test", 1000L)
        val message = matchmakingTimeoutException.message
        assertTrue(message.contains("test"))

        MatchmakingFragment.exceptionToString(Exception("test"))
        MatchmakingFragment.exceptionToString(matchmakingTimeoutException)

        val timeoutCancellationException = kotlin.runCatching {
            withTimeout(0) {
            }
        }.exceptionOrNull() as? TimeoutCancellationException
        assertNotNull(timeoutCancellationException)
        MatchmakingFragment.exceptionToString(timeoutCancellationException)
    }

    fun textDoes(predicate: (String) -> Boolean): Matcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description) {
                description.appendText("Has EditText/TextView the value:  $predicate")
            }

            override fun matchesSafely(view: View?): Boolean {
                val text = when (view) {
                    is EditText -> view.text.toString()
                    is TextView -> view.text.toString()
                    else -> return false
                }
                return predicate(text)
            }
        }
    }
}