package ch.epfl.sweng.rps

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import ch.epfl.sweng.rps.ui.settings.SettingsActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import org.hamcrest.Matcher
import org.hamcrest.Matchers.instanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Callable
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeoutException
import kotlin.test.assertEquals


@RunWith(AndroidJUnit4::class)
class SettingsPageTest {
    @get:Rule
    val testRule = ActivityScenarioRule(SettingsActivity::class.java)

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

    private inline fun <reified T : Activity> getActivityInstance(): T {
        var activity: Activity? = null
        getInstrumentation().runOnMainSync(Runnable {
            val resumedActivities: Collection<Activity> =
                ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
            Log.i("SettingsPageTest", "resumedActivities: $resumedActivities")
            if (resumedActivities.iterator().hasNext()) {
                activity = resumedActivities.iterator().next()
            }
        })
        if (activity == null) {
            throw TimeoutException("No activity found")
        }
        if (activity !is T) {
            throw IllegalStateException("Activity is not of type ${T::class.java.simpleName}")
        }
        return activity!! as T
    }

    private fun getCurrentNightMode(): Int {
        val activity = getActivityInstance<SettingsActivity>()

        val futureResult = FutureTask { AppCompatDelegate.getDefaultNightMode() }

        activity.runOnUiThread(futureResult)

        return futureResult.get()
    }

    fun <T> runOnUiThreadBlocking(runnable: Callable<T>): T {
        val activity = getActivityInstance<SettingsActivity>()
        val future = FutureTask(runnable)
        activity.runOnUiThread(future)
        return future.get()
    }

    @Test
    fun testSettingsPage() {
        onView(withId(R.id.settings)).check(matches(isDisplayed()))

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

    private fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
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
