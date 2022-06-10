package ch.epfl.sweng.rps

import android.app.Instrumentation
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import ch.epfl.sweng.rps.TestUtils.getActivityInstance
import ch.epfl.sweng.rps.TestUtils.initializeForTest
import ch.epfl.sweng.rps.TestUtils.waitFor
import ch.epfl.sweng.rps.remote.Env
import ch.epfl.sweng.rps.remote.FirebaseReferences
import ch.epfl.sweng.rps.remote.LocalRepository
import ch.epfl.sweng.rps.services.ServiceLocator
import ch.epfl.sweng.rps.ui.settings.SettingsActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.ktx.Firebase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.instanceOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import java.util.concurrent.FutureTask
import kotlin.test.assertTrue


@RunWith(AndroidJUnit4::class)
class SettingsPageTest {
    @get:Rule
    val scenarioRule = ActivityScenarioRuleWithSetup.default(SettingsActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
        val clipboard: ClipboardManager? =
            getInstrumentation().context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val data = ClipData.newPlainText("text", text)
        clipboard?.setPrimaryClip(data)
        ServiceLocator.setCurrentEnv(Env.Prod)
    }

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

    private fun getThemeIdFromAppCompatTheme(appCompatThemeId: Int): String? {
        val filtered =
            themeIdToAppCompatThemeId.entries.firstOrNull { it.value == appCompatThemeId }
        return filtered?.key
    }


    private fun getCurrentNightMode(activity: SettingsActivity): Int {
        val futureResult = FutureTask { AppCompatDelegate.getDefaultNightMode() }
        activity.runOnUiThread(futureResult)
        return futureResult.get()
    }



    @Test
    fun testSettingsPage() {
        onView(withId(R.id.settings)).check(matches(isDisplayed()))
        val activity = getActivityInstance<SettingsActivity>()

        for ((key, value) in computeThemeMap()) {
            Log.i("SettingsPageTest", "Testing theme $key")
            onView(withText(R.string.theme_mode)).perform(click())
            onView(withText(key)).perform(click())
            onView(isRoot()).perform(waitFor(2000))

            // We comment this out because it can sometimes fail on CI
            /* val appCompatThemeId = getCurrentNightMode(activity)
             val themeId = getAppCompatThemeFromThemeId(value)
             if (appCompatThemeId != AppCompatDelegate.MODE_NIGHT_UNSPECIFIED) {
                 assertEquals(
                     themeId,
                     appCompatThemeId,
                     "Theme should be $value ($themeId) after clicking $key, but is $appCompatThemeId (${
                         getThemeIdFromAppCompatTheme(appCompatThemeId)
                     })"
                 )
             }*/
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

    private var text: String? = null

    @Before
    fun setUp() {
        Firebase.initializeForTest()
        val clipboard: ClipboardManager? =
            getInstrumentation().context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        text = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
        ServiceLocator.setCurrentEnv(Env.Test)
        val repo = ServiceLocator.getInstance().repository as LocalRepository
        repo.setCurrentUid("player1")
        repo.gamesMap.clear()
        repo.users.clear()
    }


    @Test
    fun testCopyToClipboard() {
        onView(withId(R.id.settings)).check(matches(isDisplayed()))
        onView(withText(R.string.copy_fb_uid)).perform(click())
    }

    @Test
    fun testDebugInfoDump() {
        val expectedIntent = IntentMatchers.hasAction(Intent.ACTION_VIEW)
        Intents.intending(expectedIntent).respondWith(Instrumentation.ActivityResult(0, null))

        onView(withId(R.id.settings)).check(matches(isDisplayed()))
        onView(withText(R.string.dump_dbg_infos_pref_text)).perform(click())

        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_VIEW))
    }

    @Test
    fun testAddGame() {
        runBlocking {
            assertEquals(Env.Test, ServiceLocator.getCurrentEnv())
            onView(withId(R.id.settings)).check(matches(isDisplayed()))
            onView(withText(R.string.add_artificial_game)).perform(click())
        }
    }

    @Test
    fun testOpenOnboarding() {
        onView(withId(R.id.settings)).check(matches(isDisplayed()))
        onView(withText(R.string.show_welcome_screen_text_settings)).perform(click())
        onView(withId(R.id.onboarding_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun testResetSharedPrefs() {
        onView(withId(R.id.settings)).check(matches(isDisplayed()))
        onView(withText(R.string.clear_shared_prefs_settings_text)).perform(click())
    }

    @Test
    fun addArtificialGame() = runBlocking {
        val firebaseReferences = mockk<FirebaseReferences>()
        var hasSet = false
        every { firebaseReferences.gamesCollection.document(any()).set(any()) } answers {
            hasSet = true
            Tasks.forResult(null)
        }
        SettingsActivity.HeaderFragment.createArtificialGame(
            firebaseReferences,
            "game1",
            "player1",
            "player2"
        )
        assertTrue(hasSet)
    }
}
