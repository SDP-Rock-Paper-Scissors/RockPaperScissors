/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.epfl.sweng.rps.vision

import android.app.ActivityManager
import android.content.Context
import android.os.Build.VERSION_CODES
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.common.InputImage
import ch.epfl.sweng.rps.ui.camera.CameraXViewModel
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabelerOptionsBase
import com.google.mlkit.vision.label.ImageLabeling
import java.io.IOException
import java.util.*
import kotlin.math.max
import kotlin.math.min


/**
 * Abstract base class for ML Kit frame processors. Subclasses need to implement {@link
 * #onSuccess(T, FrameMetadata, GraphicOverlay)} to define what they want to with the detection
 * results and {@link #detectInImage(VisionImage)} to specify the detector object.
 *
 * @param <T> The type of the detected feature.
 */
 class LabelDetectorProcessor (context: Context, options: ImageLabelerOptionsBase, private val model: CameraXViewModel) : VisionImageProcessor {

  private var activityManager: ActivityManager =
    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
  private val fpsTimer = Timer()
  private val executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)

  // Whether this processor is already shut down
  private var isShutdown = false

  // Used to calculate latency, running in the same thread, no sync needed.
  private var numRuns = 0
  private var totalFrameMs = 0L
  private var maxFrameMs = 0L
  private var minFrameMs = Long.MAX_VALUE
  private var totalDetectorMs = 0L
  private var maxDetectorMs = 0L
  private var minDetectorMs = Long.MAX_VALUE

  // Frame count that have been processed so far in an one second interval to calculate FPS.
  private var frameProcessedInOneSecondInterval = 0
  private var framesPerSecond = 0


  init {
    fpsTimer.scheduleAtFixedRate(
      object : TimerTask() {
        override fun run() {
          framesPerSecond = frameProcessedInOneSecondInterval
          frameProcessedInOneSecondInterval = 0
        }
      },
      0,
      1000
    )
  }

  //Getting the image labeler of the ml kit
  private val imageLabeler: ImageLabeler = ImageLabeling.getClient(options)
  //For keeping track of the last three image label confidences
  private val confidenceQueueRock: Queue<Double> = LinkedList(listOf(0.0, 0.0 ,0.0))
  private val confidenceQueuePaper: Queue<Double> = LinkedList(listOf(0.0, 0.0 ,0.0))
  private val confidenceQueueScissors: Queue<Double> = LinkedList(listOf(0.0, 0.0 ,0.0))
  private val threshold: Double = 0.80





  // -----------------Code for processing live preview frame from CameraX API-----------------------

  @ExperimentalGetImage
  override fun processImageProxy(image: ImageProxy?, graphicOverlay: GraphicOverlay?) {
    val frameStartMs = SystemClock.elapsedRealtime()
    if (isShutdown) {
      return
    }

    requestDetectInImage(
      InputImage.fromMediaImage(image!!.image!!, image.imageInfo.rotationDegrees),
      graphicOverlay!!,
      /* shouldShowFps= */ true,
      frameStartMs
    )
      // When the image is from CameraX analysis use case, must call image.close() on received
      // images when finished using them. Otherwise, new images may not be received or the camera
      // may stall.
      .addOnCompleteListener { image.close() }
  }

  // -----------------Common processing logic-------------------------------------------------------
  private fun requestDetectInImage(
    image: InputImage,
    graphicOverlay: GraphicOverlay,
    shouldShowFps: Boolean,
    frameStartMs: Long
  ): Task<List<ImageLabel>> {
    return setUpListener(
      detectInImage(image),
      graphicOverlay,
      shouldShowFps,
      frameStartMs
    )
  }


  private fun setUpListener(
    task: Task<List<ImageLabel>>,
    graphicOverlay: GraphicOverlay,
    shouldShowFps: Boolean,
    frameStartMs: Long,
  ): Task<List<ImageLabel>> {
    val detectorStartMs = SystemClock.elapsedRealtime()
    return task
      .addOnSuccessListener(
        executor
      ) { results ->
          val endMs = SystemClock.elapsedRealtime()
          val currentFrameLatencyMs = endMs - frameStartMs
          val currentDetectorLatencyMs = endMs - detectorStartMs
          if (numRuns >= 500) {
              resetLatencyStats()
          }
          numRuns++
          frameProcessedInOneSecondInterval++
          totalFrameMs += currentFrameLatencyMs
          maxFrameMs = max(currentFrameLatencyMs, maxFrameMs)
          minFrameMs = min(currentFrameLatencyMs, minFrameMs)
          totalDetectorMs += currentDetectorLatencyMs
          maxDetectorMs = max(currentDetectorLatencyMs, maxDetectorMs)
          minDetectorMs = min(currentDetectorLatencyMs, minDetectorMs)

          // Only log inference info once per second. When frameProcessedInOneSecondInterval is
          // equal to 1, it means this is the first frame processed during the current second.
          if (frameProcessedInOneSecondInterval == 1) {
              Log.d(TAG, "Num of Runs: $numRuns")
              Log.d(
                  TAG,
                  "Frame latency: max=" +
                          maxFrameMs +
                          ", min=" +
                          minFrameMs +
                          ", avg=" +
                          totalFrameMs / numRuns
              )
              Log.d(
                  TAG,
                  "Detector latency: max=" +
                          maxDetectorMs +
                          ", min=" +
                          minDetectorMs +
                          ", avg=" +
                          totalDetectorMs / numRuns
              )
              val mi = ActivityManager.MemoryInfo()
              activityManager.getMemoryInfo(mi)
              val availableMegs: Long = mi.availMem / 0x100000L
              Log.d(TAG, "Memory available in system: $availableMegs MB")
          }
          graphicOverlay.clear()
          this@LabelDetectorProcessor.onSuccess(results, graphicOverlay)

          graphicOverlay.add(
              InferenceInfoGraphic(
                  graphicOverlay,
                  currentFrameLatencyMs,
                  currentDetectorLatencyMs,
                  if (shouldShowFps) framesPerSecond else null
              )
          )

          graphicOverlay.postInvalidate()
      }
        .addOnFailureListener(
        executor
        ) { e: Exception ->
            graphicOverlay.clear()
            graphicOverlay.postInvalidate()
            val error = "Failed to process. Error: " + e.localizedMessage
            Toast.makeText(
                graphicOverlay.context,
                """
              $error
              Cause: ${e.cause}
              """.trimIndent(),
                Toast.LENGTH_SHORT
            )
                .show()
            Log.d(TAG, error)
            e.printStackTrace()
            this@LabelDetectorProcessor.onFailure(e)
        }
  }

  override fun stop() {

    try {
      imageLabeler.close()
    } catch (e: IOException) {
      Log.e(
        TAG,
        "Exception thrown while trying to close ImageLabelerClient: $e"
      )
    }
    executor.shutdown()
    isShutdown = true
    resetLatencyStats()
    fpsTimer.cancel()
  }

  private fun resetLatencyStats() {
    numRuns = 0
    totalFrameMs = 0
    maxFrameMs = 0
    minFrameMs = Long.MAX_VALUE
    totalDetectorMs = 0
    maxDetectorMs = 0
    minDetectorMs = Long.MAX_VALUE
  }

  private fun detectInImage(image: InputImage): Task<List<ImageLabel>> {
    return imageLabeler.process(image)
  }

  private fun updateQueue(confidenceQueue: Queue<Double>, name: String, results: List<ImageLabel>){
    confidenceQueue.poll()
    val imageLabel = results.find { imageLabel -> name == imageLabel.text}
    confidenceQueue.add(imageLabel!!.confidence.toDouble())
  }

  private fun onSuccess(results: List<ImageLabel>, graphicOverlay: GraphicOverlay) {
    graphicOverlay.add(LabelGraphic(graphicOverlay, results))
    logExtrasForTesting(results)

    if (results.isNotEmpty()) {
      updateQueue(confidenceQueuePaper, "paper", results)
      updateQueue(confidenceQueueRock, "rock", results)
      updateQueue(confidenceQueueScissors, "scissors", results)

      if (confidenceQueuePaper.all{it > threshold} ||
        confidenceQueueRock.all{it > threshold} ||
          confidenceQueueScissors.all{it > threshold}) {
        this.model.running.postValue(results.first().text)
        stop()
      }
    }
  }

  private fun onFailure(e: Exception) {
    Log.w(TAG, "Label detection failed.$e")
  }

    companion object {
        private const val MANUAL_TESTING_LOG = "LogTagForTest"
        const val TAG = "LabelDetectorProcessor"

        fun logExtrasForTesting(labels: List<ImageLabel>?) {
            if (labels == null) {
                Log.v(MANUAL_TESTING_LOG, "No labels detected")
            } else {
                for (label in labels) {
                    Log.v(
                        MANUAL_TESTING_LOG,
                        String.format("Label %s, confidence %f", label.text, label.confidence)
                    )
                }
            }
        }
    }

}
