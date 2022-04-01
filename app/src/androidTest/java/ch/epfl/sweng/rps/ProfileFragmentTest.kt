package ch.epfl.sweng.rps

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProfileFragmentTest {
    val i: Intent = Intent()
    val b: Bundle = Bundle()
    val data = mapOf<String, String>(
        "email" to "asd@gmail.com", "display_name" to "asdino", "uid" to "123", "privacy" to "None"
    )

    @Before
    fun setUpIntent() {
        data.forEach{ b.putString(it.key, it.value)}
        i.putExtra("User", b)
    }

    @get:Rule
    val testRule = ActivityTestRule(MainActivity::class.java, false, false)

    @Test
    fun testFields() {
        testRule.launchActivity(i)
        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.LEFT))) // Left Drawer should be closed.
            .perform(DrawerActions.open()) // Open Drawer

        // Start the screen of your activity.
        Espresso.onView(ViewMatchers.withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_profile))

        onView(withId(R.id.TextDisplayName)).check(matches(withText(b.getString("display_name"))))
        onView(withId(R.id.TextEmail)).check(matches(withText(b.getString("email"))))
        onView(withId(R.id.TextPrivacy)).check(matches(withText(b.getString("privacy"))))
    }
}