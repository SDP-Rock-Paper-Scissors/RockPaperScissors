package ch.epfl.sweng.rps


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.services.ServiceLocator
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals


@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRuleWithSetup.default(MainActivity::class.java)

    @Before
    fun setUp() {
        ServiceLocator.setCurrentEnv(Env.Test)
    }

    @Test
    fun checkFirstFragment() {
        onView(withId(R.id.fragment_home)).check(matches(isDisplayed()))
    }


    @Test
    fun checkStatistics() {
        assertEquals(Env.Test, ServiceLocator.getCurrentEnv())
        onView(withId(R.id.nav_statistics)).perform(click())
        onView(withId(R.id.fragment_statistics)).check(matches(isDisplayed()))
    }
}