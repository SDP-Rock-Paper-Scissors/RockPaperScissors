package ch.epfl.sweng.rps

import android.content.Intent
import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.models.User
import org.junit.Rule
import org.junit.Test


class ProfileFragmentTest {

    private val bundle = run {
        val b: Bundle = Bundle()
        val data = mapOf(
            "email" to "asd@gmail.com",
            "display_name" to "asdino",
            "uid" to "123",
            "privacy" to User.Privacy.PUBLIC.toString()
        )
        data.forEach { (k, v) -> b.putString(k, v) }
        b
    }


    private fun createIntent(): Intent {
        val i: Intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            MainActivity::class.java
        )
        i.putExtra("User", bundle)
        return i
    }

    @get:Rule
    val testRule = ActivityScenarioRule<MainActivity>(createIntent())

    @Test
    fun testFields() {
        onView(withId(R.id.nav_profile)).perform(click())
        onView(withId(R.id.TextDisplayName)).check(matches(withText(bundle.getString("display_name"))))
        onView(withId(R.id.TextEmail)).check(matches(withText(bundle.getString("email"))))
        onView(withId(R.id.TextPrivacy)).check(matches(withText(bundle.getString("privacy"))))
    }
}