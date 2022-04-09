package ch.epfl.sweng.rps

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.times
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.storage.PrivateStorage
import ch.epfl.sweng.rps.storage.Storage
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginPageTest {
    @get:Rule
    val testRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().targetContext)
        FirebaseAuth.getInstance().signOut()
        PrivateStorage(InstrumentationRegistry.getInstrumentation().targetContext).removeFile(
            Storage.FILES.USERINFO
        )
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }


    @Test
    fun clickOnSignInShowsMainActivity() {
        onView(withId(R.id.login_view)).check(matches(isDisplayed()))
        onView(withId(R.id.signIn)).perform(click())

        Intents.intended(hasAction("com.google.android.gms.auth.GOOGLE_SIGN_IN"), times(2))
    }
}