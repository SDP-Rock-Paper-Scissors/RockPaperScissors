package ch.epfl.sweng.rps

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.times
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.TestUtils.initializeForTest
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.persistence.PrivateStorage
import ch.epfl.sweng.rps.persistence.Storage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginPageTest {
    private lateinit var scenario: ActivityScenario<LoginActivity>

    @Before
    fun setUp() {
        Firebase.initializeForTest()
        FirebaseAuth.getInstance().signOut()
        PrivateStorage(InstrumentationRegistry.getInstrumentation().targetContext).deleteFile(
            Storage.FILES.USERINFO
        )
        Intents.init()
        Cache.initialize(InstrumentationRegistry.getInstrumentation().targetContext)
            .setUserDetails(null)
        scenario = ActivityScenario.launch(LoginActivity::class.java)
    }

    @After
    fun tearDown() {
        Intents.release()
        scenario.close()
    }


    @Test
    fun clickOnSignInShowsMainActivity() {
        onView(withId(R.id.login_view)).check(matches(isDisplayed()))
        onView(withId(R.id.signIn)).perform(click())

        Intents.intended(hasAction("com.google.android.gms.auth.GOOGLE_SIGN_IN"), times(2))
    }
}