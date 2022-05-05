package ch.epfl.sweng.rps.vision

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
//@Config(sdk = [Build.VERSION_CODES.O_MR1]) //needed unless you run your tests with java 9
class LabelGraphicTest {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity
    private lateinit var overlay: GraphicOverlay

    @Before
    fun setUp() {
        // Create an activity (Can be any sub-class: i.e. AppCompatActivity, FragmentActivity, etc)
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        // Create the view using the activity context

        var overlayView = overlay(activity!!)
    }
}

