package ch.epfl.sweng.rps.vision


import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sweng.rps.MainActivity
import ch.epfl.sweng.rps.R
import com.google.mlkit.vision.label.ImageLabel
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test


class GraphicOverlayTest {

    private fun createIntent(): Intent {
        return Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            GraphicOverlayActivity::class.java
        )
    }


    @get:Rule
    val activityTestRule = ActivityScenarioRule(GraphicOverlayActivity::class.java)



    @Test
    fun testAddRemove(){
        activityTestRule.scenario.onActivity {activity ->
            val graphicOverlay = activity.findViewById<GraphicOverlay>(R.id.graphic_overlay_test)

            val results: MutableList<ImageLabel> = mutableListOf(ImageLabel("test", 0.5F, 1))
            val lgraphic = LabelGraphic(graphicOverlay, results)
            graphicOverlay.add(lgraphic)
            assertEquals(graphicOverlay.graphics.first(), lgraphic)
            // Now removing
            graphicOverlay.remove(lgraphic)
            assertTrue(graphicOverlay.graphics.isEmpty())
        }
    }


    @Test
    fun testClean () {
        activityTestRule.scenario.onActivity { activity ->
            val graphicOverlay =
                activity.findViewById<GraphicOverlay>(R.id.graphic_overlay_test)

            val results: MutableList<ImageLabel> = mutableListOf(ImageLabel("test", 0.5F, 1))
            val lgraphic = LabelGraphic(graphicOverlay, results)
            graphicOverlay.add(lgraphic)
            graphicOverlay.add(lgraphic)

            graphicOverlay.clear()
            assertTrue(graphicOverlay.graphics.isEmpty())
        }
    }



    @Test
    fun testUpdateTransformationIfNeeded1() {
        activityTestRule.scenario.onActivity { activity ->
            val graphicOverlay =
                activity.findViewById<GraphicOverlay>(R.id.graphic_overlay_test)
            //Inflate the layout to so the view has a height of 10 and width of 10
            val imageWidth = 10
            val imageHeight = 10
            val sourceWidth = 20f
            val sourceHeight = 10f
            val imageAspectRatio = imageWidth.toFloat() / imageHeight


            graphicOverlay.layout(0, 0, imageWidth, imageHeight)

            graphicOverlay.setImageSourceInfo(20, 10, false)
            graphicOverlay.updateTransformationIfNeeded()

            assertEquals(sourceHeight / imageHeight, graphicOverlay.scaleFactor)
            assertEquals(
                -(sourceHeight * imageAspectRatio - sourceWidth) / 2,
                graphicOverlay.postScaleWidthOffset
            )
        }
    }

    @Test
    fun testUpdateTransformationIfNeeded2() {
        activityTestRule.scenario.onActivity { activity ->
            val graphicOverlay =
                activity.findViewById<GraphicOverlay>(R.id.graphic_overlay_test)
            //Inflate the layout to so the view has a height of 10 and width of 10
            val imageWidth = 20
            val imageHeight = 10
            val sourceWidth = 20f
            val sourceHeight = 10f
            val imageAspectRatio = imageWidth.toFloat() / imageHeight

            graphicOverlay.layout(0, 0, imageWidth, imageHeight)

            graphicOverlay.setImageSourceInfo(20, 10, false)
            graphicOverlay.updateTransformationIfNeeded()

            assertEquals(sourceWidth / imageWidth, graphicOverlay.scaleFactor)
            assertEquals(
                (sourceWidth / imageAspectRatio - sourceHeight) / 2,
                graphicOverlay.postScaleHeightOffset
            )
        }
    }

}

