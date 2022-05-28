package ch.epfl.sweng.rps

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.TestUtils.initializeForTest
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.persistence.PrivateStorage
import ch.epfl.sweng.rps.persistence.Storage
import ch.epfl.sweng.rps.remote.Env
import ch.epfl.sweng.rps.services.ServiceLocator
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameButtonsTest {
    lateinit var cache: Cache
    lateinit var storage: Storage

    @get:Rule
    val testRule = ActivityScenarioRuleWithSetup.default(MainActivity::class.java)

    @Before
    fun setUp() {
        ServiceLocator.setCurrentEnv(Env.Test)
        ServiceLocator.localRepository.setCurrentUid("test")
        Firebase.initializeForTest()
        cache = Cache.initialize(InstrumentationRegistry.getInstrumentation().targetContext)
        storage = PrivateStorage(InstrumentationRegistry.getInstrumentation().targetContext)
        runBlocking {
            cache.setUserDetails(User(uid = "user_test_uid", username = "test name"))
        }
    }

    @After
    fun tearDown() {
        ServiceLocator.setCurrentEnv(Env.Prod)
    }

    @Test
    fun pressedRock() {
        checkPressedButton(R.id.rockIM)
    }

    @Test
    fun pressedPaper() {
        checkPressedButton(R.id.paperIM)
    }

    @Test
    fun pressedScissors() {
        checkPressedButton(R.id.scissorsIM)
    }

    private fun checkPressedButton(radioButtonId: Int) = runBlocking {
        onView(withId(R.id.button_play_1_games_offline)).perform(click())
        onView(withId(radioButtonId)).perform(click())
        onView(withId(radioButtonId)).check(matches(isDisplayed()))

    }
}

