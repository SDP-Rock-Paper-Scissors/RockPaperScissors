package ch.epfl.sweng.rps

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.times
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import ch.epfl.sweng.rps.storage.PrivateStorage
import ch.epfl.sweng.rps.storage.Storage
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginPageTest {
    @get:Rule
    val testRule = ActivityTestRule(LoginActivity::class.java, false, false)

    @Before
    fun setUp() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
        FirebaseAuth.getInstance().signOut()
        PrivateStorage(ApplicationProvider.getApplicationContext()).removeFile(Storage.FILES.USERINFO)
    }


    @Test
    fun clickOnSignInShowsMainActivity() {
        testRule.launchActivity(Intent())
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.login_view)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.signIn)).perform(ViewActions.click())

        Intents.intended(hasAction("com.google.android.gms.auth.GOOGLE_SIGN_IN"), times(2))
        Intents.release()
    }
}