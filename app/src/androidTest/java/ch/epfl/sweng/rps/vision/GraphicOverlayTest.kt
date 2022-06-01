package ch.epfl.sweng.rps.vision


import ch.epfl.sweng.rps.ActivityScenarioRuleWithSetup
import ch.epfl.sweng.rps.ActivityScenarioRuleWithSetup.Companion.defaultTestFlow
import ch.epfl.sweng.rps.R
import com.google.mlkit.vision.label.ImageLabel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test


class GraphicOverlayTest {

    @get:Rule
    val activityTestRule =
        ActivityScenarioRuleWithSetup(GraphicOverlayActivity::class.java, defaultTestFlow)


    @Test
    fun testAddRemove() {
        activityTestRule.scenario.onActivity { activity ->
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
    fun testClean() {
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

    @Test
    fun scaleTest() {
        activityTestRule.scenario.onActivity { activity ->
            val graphicOverlay =
                activity.findViewById<GraphicOverlay>(R.id.graphic_overlay_test)

            val results: MutableList<ImageLabel> = mutableListOf(ImageLabel("test", 0.5F, 1))
            val lgraphic = LabelGraphic(graphicOverlay, results)
            assertEquals(5.0f, lgraphic.scale(5.0f))

        }
    }

    @Test
    fun translatexFlippedTest() {
        activityTestRule.scenario.onActivity { activity ->
            val graphicOverlay =
                activity.findViewById<GraphicOverlay>(R.id.graphic_overlay_test)
            graphicOverlay.isImageFlipped = true

            val results: MutableList<ImageLabel> = mutableListOf(ImageLabel("test", 0.5F, 1))
            val lgraphic = LabelGraphic(graphicOverlay, results)
            val expected: Float =
                graphicOverlay.width - (lgraphic.scale(5.0f) - graphicOverlay.postScaleWidthOffset)
            assertEquals(expected, lgraphic.translateX(5.0f))

        }
    }

    @Test
    fun translatexNotFlippedTest() {
        activityTestRule.scenario.onActivity { activity ->
            val graphicOverlay =
                activity.findViewById<GraphicOverlay>(R.id.graphic_overlay_test)
            graphicOverlay.isImageFlipped = false

            val results: MutableList<ImageLabel> = mutableListOf(ImageLabel("test", 0.5F, 1))
            val lgraphic = LabelGraphic(graphicOverlay, results)
            val expected: Float = lgraphic.scale(5.0f) - graphicOverlay.postScaleWidthOffset
            assertEquals(expected, lgraphic.translateX(5.0f))

        }
    }

    fun translateYTest() {
        activityTestRule.scenario.onActivity { activity ->
            val graphicOverlay =
                activity.findViewById<GraphicOverlay>(R.id.graphic_overlay_test)

            val results: MutableList<ImageLabel> = mutableListOf(ImageLabel("test", 0.5F, 1))
            val lgraphic = LabelGraphic(graphicOverlay, results)
            val expected: Float = lgraphic.scale(5.0f) - graphicOverlay.postScaleHeightOffset
            assertEquals(expected, lgraphic.translateY(5.0f))

        }
    }

}

