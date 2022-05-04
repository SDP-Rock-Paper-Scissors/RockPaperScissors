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

import android.content.Context
import android.util.Log
import ch.epfl.sweng.rps.ui.camera.CameraXViewModel
import ch.epfl.sweng.rps.ui.camera.LabelGraphic
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabelerOptionsBase
import com.google.mlkit.vision.label.ImageLabeling
import java.io.IOException



/** Custom InputImage Classifier Demo.  */
class LabelDetectorProcessor(context: Context, options: ImageLabelerOptionsBase, private val model: CameraXViewModel) :
  VisionProcessorBase<List<ImageLabel>>(context) {
  
  private val imageLabeler: ImageLabeler = ImageLabeling.getClient(options)



  override fun stop() {
    super.stop()

    try {
      imageLabeler.close()
    } catch (e: IOException) {
      Log.e(
        TAG,
        "Exception thrown while trying to close ImageLabelerClient: $e"
      )
    }
  }

  override fun detectInImage(image: InputImage): Task<List<ImageLabel>> {
    return imageLabeler.process(image)
  }

  override fun onSuccess(results: List<ImageLabel>, graphicOverlay: GraphicOverlay) {
    graphicOverlay.add(LabelGraphic(graphicOverlay, results))
    logExtrasForTesting(results)
    if(!results.isNullOrEmpty() && results.first().confidence > 0.85) {
        this.model.running.postValue(results.first().text)
      stop()
      }
  }


  override fun onFailure(e: Exception) {
    Log.w(TAG, "Label detection failed.$e")
  }

  companion object {
    private const val TAG = "LabelDetectorProcessor"

    private fun logExtrasForTesting(labels: List<ImageLabel>?) {
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
