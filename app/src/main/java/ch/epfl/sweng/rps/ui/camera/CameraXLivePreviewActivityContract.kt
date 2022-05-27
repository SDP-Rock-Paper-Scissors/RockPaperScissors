package ch.epfl.sweng.rps.ui.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ch.epfl.sweng.rps.models.remote.Hand

class CameraXLivePreviewActivityContract : ActivityResultContract<String?, Hand?>() {

    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(context, CameraXLivePreviewActivity::class.java)

    }

    override fun parseResult(resultCode: Int, intent: Intent?): Hand? = when {
        resultCode != Activity.RESULT_OK -> null
        else -> convertToHand(intent?.getStringExtra("result")!!)
    }

    fun convertToHand(result: String): Hand {
        return when (result) {
            "rock" -> Hand.ROCK
            "paper" -> Hand.PAPER
            "scissors" -> Hand.SCISSORS
            else -> Hand.NONE

        }
    }
}
