package com.example.mlseriesdemo.Image

import android.graphics.Bitmap
import android.os.Bundle
import com.example.mlseriesdemo.helpers.BoxWithLabel
import com.example.mlseriesdemo.helpers.ImageHelperActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetctionActivity : ImageHelperActivity() {
    lateinit var faceDetector: com.google.mlkit.vision.face.FaceDetector
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val highAccarucyOpts: FaceDetectorOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .enableTracking()
            .build()

        faceDetector = FaceDetection.getClient(highAccarucyOpts)
    }

    override fun runClassification(bitmap: Bitmap) {
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val inputImage: InputImage = InputImage.fromBitmap(outputBitmap, 0)

        faceDetector.process(inputImage)
            .addOnSuccessListener {
                if (it.isEmpty()) {
                    getOutputTextView().setText("No face detected")
                } else {
                    val boxes: ArrayList<BoxWithLabel> = ArrayList()
                    for (face in it) {
                        val boxWithLabel =
                            BoxWithLabel(face.boundingBox, face.trackingId.toString() + "")
                        boxes.add(boxWithLabel)
                    }
                    drowDetectionResult(boxes, outputBitmap)
                    binding.tvOutput.text = it.size.toString()
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}