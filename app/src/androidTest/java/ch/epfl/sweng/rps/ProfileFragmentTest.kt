package ch.epfl.sweng.rps

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.services.ServiceLocator
import org.junit.Rule
import org.junit.Test


class ProfileFragmentTest {

    private fun createIntent(): Intent {
        return Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            MainActivity::class.java
        )
    }

    @get:Rule
    val testRule = ActivityScenarioRuleWithSetup.default<MainActivity>(createIntent())

    @Test
    fun testFields() {
        val user = ServiceLocator.localRepository.let { it.users[it.getCurrentUid()] }!!
        Cache.getInstance().setUserDetails(user)
        onView(withId(R.id.nav_profile)).perform(click())
        onView(withText(user.username)).check(matches(isDisplayed()))
        onView(withText(user.email)).check(matches(isDisplayed()))
    }

    @Test
    fun tapSettings() {
        onView(withId(R.id.nav_profile)).perform(click())
        onView(withId(R.id.profile_appbar_settings_btn)).perform(click())
        onView(withId(R.id.settings)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.profile_appbar_settings_btn)).check(matches(isDisplayed()))
    }

    @Test
    fun tapEditStartsIntent() {
        Intents.init()
        onView(withId(R.id.nav_profile)).perform(click())
        onView(withId(R.id.editProfilePic)).perform(click())
        assert(Intents.getIntents().size == 1)
        Intents.release()
    }

}