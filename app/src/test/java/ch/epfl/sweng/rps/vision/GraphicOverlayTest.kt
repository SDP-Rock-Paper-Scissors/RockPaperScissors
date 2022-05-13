package ch.epfl.sweng.rps.vision

import android.app.Activity
import android.graphics.Matrix
import android.os.Build
import android.widget.LinearLayout
import com.google.mlkit.vision.label.ImageLabel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class LabelGraphicTest {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity
    private lateinit var graphicOverlay: GraphicOverlay



    @Before
    fun setUp() {
        // Create an activity (Can be any sub-class: i.e. AppCompatActivity, FragmentActivity, etc)
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        // Create the view using the activity context
        graphicOverlay = GraphicOverlay(activity)

    }

    @Test
    fun testAddRemove(){
        val results: MutableList<ImageLabel> = mutableListOf(ImageLabel("test",0.5F, 1))
        val lgraphic = LabelGraphic(graphicOverlay, results)
        graphicOverlay.add(lgraphic)
        assertEquals(graphicOverlay.graphics.first(),  lgraphic)
        // Now removing
        graphicOverlay.remove(lgraphic)
        assertTrue(graphicOverlay.graphics.isEmpty())
    }

    @Test
    fun testClean (){
        val results: MutableList<ImageLabel> = mutableListOf(ImageLabel("test",0.5F, 1))
        val lgraphic = LabelGraphic(graphicOverlay, results)
        graphicOverlay.add(lgraphic)
        graphicOverlay.add(lgraphic)

        graphicOverlay.clear()
        assertTrue(graphicOverlay.graphics.isEmpty())
    }

    @Test
    fun testUpdateTransformationIfNeeded1() {
        //Inflate the layout to so the view has a height of 10 and width of 10
        val imageWidth = 10
        val imageHeight = 10
        val sourceWidth = 20f
        val sourceHeight = 10f
        val imageAspectRatio = imageWidth.toFloat() / imageHeight


        graphicOverlay.layout(0,0, imageWidth, imageHeight)

        graphicOverlay.setImageSourceInfo(20, 10, false)
        graphicOverlay.updateTransformationIfNeeded()

        assertEquals(sourceHeight / imageHeight ,graphicOverlay.scaleFactor)
        assertEquals(- (sourceHeight * imageAspectRatio - sourceWidth) / 2 ,graphicOverlay.postScaleWidthOffset)

    }

    @Test
    fun testUpdateTransformationIfNeeded2() {
        //Inflate the layout to so the view has a height of 10 and width of 10
        val imageWidth = 20
        val imageHeight = 10
        val sourceWidth = 20f
        val sourceHeight = 10f
        val imageAspectRatio = imageWidth.toFloat() / imageHeight

        graphicOverlay.layout(0,0, imageWidth, imageHeight)

        graphicOverlay.setImageSourceInfo(20, 10, false)
        graphicOverlay.updateTransformationIfNeeded()

        assertEquals(sourceWidth / imageWidth ,graphicOverlay.scaleFactor)
        assertEquals((sourceWidth / imageAspectRatio - sourceHeight) / 2 ,graphicOverlay.postScaleHeightOffset)

    }

}

