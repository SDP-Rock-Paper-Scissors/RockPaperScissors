package ch.epfl.sweng.rps

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test


class MatchmakingTest {

    @get:Rule
    val rule = ActivityScenarioRuleWithSetup.default(MainActivity::class.java)

    @Test
    fun testMatchmaking() {
        onView(withId(R.id.button_play_1_games_online)).perform(click())
        onView(withId(R.id.matchmaking_fragment)).check(matches(isDisplayed()))
        onView(withId(R.id.matchmaking_status_textview)).check(matches(textDoes {
            it.lowercase().contains("error")
        }))
    }

    fun textDoes(predicate: (String) -> Boolean): Matcher<View?>? {
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