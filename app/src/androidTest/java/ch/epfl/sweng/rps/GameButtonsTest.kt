package ch.epfl.sweng.rps

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
// temporarily stop the tests, new behaviour needed
// their state can't be checked, new screen is loaded
//@RunWith(AndroidJUnit4::class)
//@SmallTest
//class GameButtonsTest {
//    @get:Rule
//    val testRule = ActivityScenarioRule(MainActivity::class.java)
//    @Test
//    fun pressedRock(){
//        checkPressedButton(R.id.rockRB)
//    }
//    @Test
//    fun pressedPaper(){
//        checkPressedButton(R.id.paperRB)
//    }
//    @Test
//    fun pressedScissors() {
//        checkPressedButton(R.id.scissorsRB)
//    }
//    private fun checkPressedButton(radioButtonId: Int){
//        onView(withId(R.id.button_play_one_offline_game)).perform(click())
//        onView(withId(radioButtonId)).perform(click())
//        onView(withId(radioButtonId)).check(matches(isChecked()))
//    }
//    @Test
//    fun pressedRockPaperRock(){
//        onView(withId(R.id.button_play_one_offline_game)).perform(click())
//        onView(withId(R.id.rockRB)).perform(click())
//        onView(withId(R.id.paperRB)).perform(click())
//        onView(withId(R.id.rockRB)).perform(click())
//        onView(withId(R.id.rockRB)).check(matches(isChecked()))
//        onView(withId(R.id.paperRB)).check(matches(not(isChecked())))
//        onView(withId(R.id.scissorsRB)).check(matches(not(isChecked())))
//    }
//
//
//}

