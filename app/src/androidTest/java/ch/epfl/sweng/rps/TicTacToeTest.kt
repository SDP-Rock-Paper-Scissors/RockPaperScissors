package ch.epfl.sweng.rps

import android.content.Intent
import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.ActivityScenarioRuleWithSetup.Companion.defaultTestFlow
import ch.epfl.sweng.rps.TestUtils.initializeForTest
import ch.epfl.sweng.rps.models.User
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matchers.equalTo
import org.junit.Rule
import org.junit.Test

class TicTacToeTest {

    private val bundle = run {
        val b: Bundle = Bundle()
        val data = mapOf(
            "email" to "asd@gmail.com",
            "display_name" to "asdino",
            "uid" to "123",
            "privacy" to User.Privacy.PUBLIC.toString()
        )
        data.forEach { (k, v) -> b.putString(k, v) }
        b
    }


    private fun createIntent(): Intent {
        Firebase.initializeForTest()
        val i = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            MainActivity::class.java
        )
        i.putExtra("User", bundle)
        return i
    }

    @get:Rule
    val testRule = ActivityScenarioRuleWithSetup<MainActivity>(createIntent(),
        defaultTestFlow then TestFlow.sameSetupAndTearDown { Thread.sleep(1000) })

    @Test
    fun opensTicTecToeChoiceFragmentTest() {
        onView(withId(R.id.button_tik_tac_toe)).perform(click())
        onView(withId(R.id.ticTacToeChoiceFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun chooseCrossTest() {
        onView(withId(R.id.button_tik_tac_toe)).perform(click())
        onView(withId(R.id.ai_pick_side_cross_radio)).perform(click())
        onView(withId(R.id.ai_pick_side_continue_btn)).perform(click())
        onView(withId(R.id.fragment_tictactoe)).check(matches(isDisplayed()))
        onView(withId(R.id.img_1)).perform(click())
        onView(withId(R.id.img_1)).check(matches(withTagValue(equalTo(R.drawable.cross))))
    }

    @Test
    fun chooseNoughtTest() {
        onView(withId(R.id.button_tik_tac_toe)).perform(click())
        onView(withId(R.id.ai_pick_side_circle_radio)).perform(click())
        onView(withId(R.id.ai_pick_side_continue_btn)).perform(click())
        onView(withId(R.id.fragment_tictactoe)).check(matches(isDisplayed()))
        onView(withId(R.id.img_5)).perform(click())
        onView(withId(R.id.img_5)).check(matches(withTagValue(equalTo(R.drawable.nought))))
    }

    @Test
    fun multiplayerChooseCrossTest() {
        onView(withId(R.id.button_tik_tac_toe)).perform(click())
        onView(withId(R.id.ai_pick_side_cross_radio)).perform(click())
        onView(withId(R.id.multButton)).perform(click())
        onView(withId(R.id.fragment_tictactoe)).check(matches(isDisplayed()))
        onView(withId(R.id.img_5)).perform(click())
        onView(withId(R.id.img_5)).check(matches(withTagValue(equalTo(R.drawable.cross))))
        onView(withId(R.id.img_1)).perform(click())
        onView(withId(R.id.img_1)).check(matches(withTagValue(equalTo(R.drawable.nought))))
    }

    @Test
    fun multiplayerChooseCircleTest() {
        onView(withId(R.id.button_tik_tac_toe)).perform(click())
        onView(withId(R.id.ai_pick_side_circle_radio)).perform(click())
        onView(withId(R.id.multButton)).perform(click())
        onView(withId(R.id.fragment_tictactoe)).check(matches(isDisplayed()))
        onView(withId(R.id.img_5)).perform(click())
        onView(withId(R.id.img_5)).check(matches(withTagValue(equalTo(R.drawable.nought))))
        onView(withId(R.id.img_9)).perform(click())
        onView(withId(R.id.img_9)).check(matches(withTagValue(equalTo(R.drawable.cross))))
    }

    @Test
    fun testCompleteMultGame() {
        onView(withId(R.id.button_tik_tac_toe)).perform(click())
        onView(withId(R.id.ai_pick_side_cross_radio)).perform(click())
        onView(withId(R.id.multButton)).perform(click())
        onView(withId(R.id.fragment_tictactoe)).check(matches(isDisplayed()))
        onView(withId(R.id.img_1)).perform(click())
        onView(withId(R.id.img_1)).check(matches(withTagValue(equalTo(R.drawable.cross))))
        onView(withId(R.id.img_4)).perform(click())
        onView(withId(R.id.img_4)).check(matches(withTagValue(equalTo(R.drawable.nought))))
        onView(withId(R.id.img_2)).perform(click())
        onView(withId(R.id.img_2)).check(matches(withTagValue(equalTo(R.drawable.cross))))
        onView(withId(R.id.img_5)).perform(click())
        onView(withId(R.id.img_5)).check(matches(withTagValue(equalTo(R.drawable.nought))))
        onView(withId(R.id.img_3)).perform(click())
        onView(withId(R.id.img_3)).check(matches(withTagValue(equalTo(R.drawable.cross))))
        onView(withId(R.id.outcomeTTT)).check(matches(isDisplayed()))
        onView(withId(R.id.outcomeTTT)).check(matches(withText("CROSS WINS")))
    }
    @Test
    fun testCompleteMultGameWithDraw(){
        onView(withId(R.id.button_tik_tac_toe)).perform(click())
        onView(withId(R.id.ai_pick_side_cross_radio)).perform(click())
        onView(withId(R.id.multButton)).perform(click())
        onView(withId(R.id.fragment_tictactoe)).check(matches(isDisplayed()))
        onView(withId(R.id.img_2)).perform(click())
        onView(withId(R.id.img_2)).check(matches(withTagValue(equalTo(R.drawable.cross))))
        onView(withId(R.id.img_1)).perform(click())
        onView(withId(R.id.img_1)).check(matches(withTagValue(equalTo(R.drawable.nought))))
        onView(withId(R.id.img_5)).perform(click())
        onView(withId(R.id.img_5)).check(matches(withTagValue(equalTo(R.drawable.cross))))
        onView(withId(R.id.img_4)).perform(click())
        onView(withId(R.id.img_4)).check(matches(withTagValue(equalTo(R.drawable.nought))))
        onView(withId(R.id.img_7)).perform(click())
        onView(withId(R.id.img_7)).check(matches(withTagValue(equalTo(R.drawable.cross))))
        onView(withId(R.id.img_8)).perform(click())
        onView(withId(R.id.img_8)).check(matches(withTagValue(equalTo(R.drawable.nought))))
        onView(withId(R.id.img_9)).perform(click())
        onView(withId(R.id.img_9)).check(matches(withTagValue(equalTo(R.drawable.cross))))
        onView(withId(R.id.img_3)).perform(click())
        onView(withId(R.id.img_3)).check(matches(withTagValue(equalTo(R.drawable.nought))))
        onView(withId(R.id.img_6)).perform(click())
        onView(withId(R.id.img_6)).check(matches(withTagValue(equalTo(R.drawable.cross))))
        onView(withId(R.id.outcomeTTT)).check(matches(isDisplayed()))
        onView(withId(R.id.outcomeTTT)).check(matches(withText("DRAW")))
    }
}