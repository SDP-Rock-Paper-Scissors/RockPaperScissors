package ch.epfl.sweng.rps

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import ch.epfl.sweng.rps.TestUtils.getActivityInstance
import ch.epfl.sweng.rps.TestUtils.waitFor
import ch.epfl.sweng.rps.ui.settings.SettingsActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import org.hamcrest.Matchers.instanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import java.util.concurrent.FutureTask


@RunWith(AndroidJUnit4::class)
class SettingsPageTest {
    @get:Rule
    val scenarioRule = ActivityScenarioRule(SettingsActivity::class.java)

    private fun computeThemeMap(): List<Map.Entry<String, String>> {
        val targetContext = getInstrumentation().targetContext
        val entries = targetContext.resources.getStringArray(R.array.theme_entries)
        val values = targetContext.resources.getStringArray(R.array.theme_values)
        val l = entries.zip(values)
        return (l + l).toMap().entries.toList()
    }

    private val themeIdToAppCompatThemeId = mapOf(
        "light" to AppCompatDelegate.MODE_NIGHT_NO,
        "dark" to AppCompatDelegate.MODE_NIGHT_YES,
        "system" to AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    )

    private fun getAppCompatThemeFromThemeId(themeId: String): Int {
        return themeIdToAppCompatThemeId[themeId]!!
    }

    private fun getThemeIdFromAppCompatTheme(appCompatThemeId: Int): String {
        val filtered = themeIdToAppCompatThemeId.entries.filter { it.value == appCompatThemeId }
        if (filtered.isEmpty()) {
            throw IllegalArgumentException("No theme id found for appCompatThemeId $appCompatThemeId")
        }
        return filtered.first().key
    }


    private fun getCurrentNightMode(): Int {
        val activity = getActivityInstance<SettingsActivity>()

        val futureResult = FutureTask { AppCompatDelegate.getDefaultNightMode() }

        activity.runOnUiThread(futureResult)

        return futureResult.get()
    }

    @Test
    fun testSettingsPage() {
        onView(withId(R.id.settings)).check(matches(isDisplayed()))

        // the activity's onCreate, onStart and onResume methods have been called at this point
        scenarioRule.scenario.moveToState(Lifecycle.State.STARTED)
        // the activity's onPause method has been called at this point
        scenarioRule.scenario.moveToState(Lifecycle.State.RESUMED)

        for (entry in computeThemeMap()) {
            Log.i("SettingsPageTest", "Testing theme ${entry.key}")
            onView(withText(R.string.theme_mode)).perform(click())
            onView(withText(entry.key)).perform(click())
            onView(isRoot()).perform(waitFor(1000))

            val appCompatThemeId = getCurrentNightMode()

            assertEquals(
                getAppCompatThemeFromThemeId(entry.value),
                appCompatThemeId,
                "Theme should be ${entry.value} (${getAppCompatThemeFromThemeId(entry.value)}) after clicking ${entry.key}, but is $appCompatThemeId (${
                    getThemeIdFromAppCompatTheme(appCompatThemeId)
                })"
            )

        }
    }


    @Test
    fun testSettingsPageLicense() {
        onView(withId(R.id.settings)).check(matches(isDisplayed()))
        onView(withText(R.string.license_title)).perform(click())

        val currentActivity = getActivityInstance<OssLicensesMenuActivity>()
        assertThat(currentActivity, instanceOf(OssLicensesMenuActivity::class.java))
        onView(withText(R.string.oss_license_title)).check(matches(isDisplayed()))
    }
}
