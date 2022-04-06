package ch.epfl.sweng.rps

import android.content.Intent
import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class ProfileFragmentTest {
    private val i: Intent = Intent()
    private val b: Bundle = Bundle()
    private val data = mapOf<String, String>(
        "email" to "asd@gmail.com", "display_name" to "asdino", "uid" to "123", "privacy" to "None"
    )

    @Before
    fun setUpIntent() {
        data.forEach { b.putString(it.key, it.value) }
        i.putExtra("User", b)
    }

    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testFields() {

        onView(withId(R.id.nav_profile)).perform(click())
        onView(withId(R.id.TextDisplayName)).check(matches(withText(b.getString("display_name"))))
        onView(withId(R.id.TextEmail)).check(matches(withText(b.getString("email"))))
        onView(withId(R.id.TextPrivacy)).check(matches(withText(b.getString("privacy"))))
    }
}