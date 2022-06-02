package ch.epfl.sweng.rps.utils

import android.app.Instrumentation.ActivityResult
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.sweng.rps.ActivityScenarioRuleWithSetup
import ch.epfl.sweng.rps.MainActivity
import ch.epfl.sweng.rps.R
import okhttp3.internal.toHexString
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DebugInfoDumpTest {

    @get:Rule
    val rule = ActivityScenarioRuleWithSetup.default(MainActivity::class.java)

    lateinit var expectedIntent: Matcher<Intent>

    @Before
    fun setup() {
        Intents.init()
        expectedIntent = hasAction(Intent.ACTION_VIEW)
        intending(expectedIntent).respondWith(ActivityResult(0, null))
    }

    @After
    fun teardown() {
        Intents.release()
    }

    @Test
    fun testDebugInfoDump() {
        val s = System.currentTimeMillis().toHexString()
        val s2 = System.currentTimeMillis().toHexString()

        rule.scenario.onActivity { a ->
            a.lifecycleScope.launchWhenStarted {
                L.of("test").i(s)
                L.of("test").e(s2, Exception(s2))
                val file = dumpDebugInfos(a, Exception("test"))
                assertTrue(file.exists())
                val txt = file.readText()
                assertTrue(txt.contains(s))
                assertTrue(txt.contains(s2))
                openJsonFile(a, file)
            }
        }
        onView(withId(R.id.container)).check(matches(isDisplayed()))
        Thread.sleep(1000)
        intended(expectedIntent)
    }
}