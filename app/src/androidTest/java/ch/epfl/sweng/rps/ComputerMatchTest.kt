package ch.epfl.sweng.rps

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.TestUtils.initializeForTest
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.persistence.PrivateStorage
import ch.epfl.sweng.rps.persistence.Storage
import ch.epfl.sweng.rps.remote.Env
import ch.epfl.sweng.rps.remote.LocalRepository
import ch.epfl.sweng.rps.services.ServiceLocator
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComputerMatchTest {
    lateinit var cache: Cache
    lateinit var storage: Storage

    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

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
        val repo = ServiceLocator.getInstance().repository as LocalRepository
        repo.setCurrentUid(null)
        ServiceLocator.setCurrentEnv(Env.Prod)
    }

    private suspend fun run1roundGame() {
        val buttonId = R.id.paperIM
        Espresso.onView(ViewMatchers.withId(R.id.button_play_1_games_offline))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(buttonId)).perform(ViewActions.click())
        delay(3_000L)
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