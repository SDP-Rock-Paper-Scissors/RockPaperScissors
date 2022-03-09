package ch.epfl.sweng.rps

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class MainActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun pressingGreetingButton(){
        val name = "John"

        Espresso.onView(ViewMatchers.withId(R.id.nameInput)).perform(ViewActions.clearText()).perform(ViewActions.typeText(name))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.greetingButton)).perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.greetingText)).check(matches(withText(name)))
    }
}