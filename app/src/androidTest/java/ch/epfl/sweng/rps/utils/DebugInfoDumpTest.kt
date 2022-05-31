package ch.epfl.sweng.rps.utils

import android.app.Instrumentation.ActivityResult
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.sweng.rps.ActivityScenarioRuleWithSetup
import ch.epfl.sweng.rps.MainActivity
import okhttp3.internal.toHexString
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

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun teardown() {
        Intents.release()
    }

    @Test
    fun testDebugInfoDump() {
        val s = System.currentTimeMillis().toHexString()
        val expectedIntent = hasAction(Intent.ACTION_VIEW)
        intending(expectedIntent).respondWith(ActivityResult(0, null))
        rule.scenario.onActivity { a ->
            a.lifecycleScope.launchWhenStarted {
                L.of("test").i(s)
                val file = dumpDebugInfos(a, Exception("test"))
                assertTrue(file.exists())
                assertTrue(file.readText().contains(s))
                openJsonFile(a, file)

            }
        }
        intended(hasAction(Intent.ACTION_VIEW))
    }
}