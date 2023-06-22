package org.tensorflow.lite.examples.detection

import android.app.Application
import android.util.Log
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import org.tensorflow.lite.examples.detection.tflite.SimilarityClassifier

class GGGApplication : Application() {
    private var faceDetector: FaceDetector? = null
    private var detector: SimilarityClassifier? = null
    override fun onCreate() {
        super.onCreate()

        // Real-time contour detection of multiple faces


        // Real-time contour detection of multiple faces
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setContourMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()


        faceDetector = FaceDetection.getClient(options)
    }

    fun getFaceDetector() = faceDetector!!
    fun updateDetector(similarityClassifier: SimilarityClassifier) {
        if (detector == null) {
            Log.d("!!!", "!!!updateDetector")
            detector = similarityClassifier;
        }
    }

    fun getDetector(): SimilarityClassifier = detector!!
}