package ch.epfl.sweng.rps.vision


import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.ui.camera.CameraXLivePreviewActivity
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainCameraTest {
    @get:Rule
    val testRule = ActivityScenarioRule(CameraXLivePreviewActivity::class.java)


    @Test
    fun checkPreview() {
        onView(withId(R.id.preview_view)).check(matches(isDisplayed()))
        onView(withId(R.id.graphic_overlay)).check(matches(isDisplayed()))
    }

}
