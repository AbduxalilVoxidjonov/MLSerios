package com.example.mlseriesdemo.Image

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import com.example.mlseriesdemo.helpers.BoxWithLabel
import com.example.mlseriesdemo.helpers.ImageHelperActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import java.lang.Exception

class ObjectDetectionActivity : ImageHelperActivity() {

    private lateinit var objectDetection: ObjectDetector

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Multiple object detection in static images
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()  // Optional
            .build()

        objectDetection = ObjectDetection.getClient(options)
    }

    @Override
    override fun runClassification(bitmap: Bitmap) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        objectDetection.process(inputImage)
            .addOnSuccessListener(object : OnSuccessListener<List<DetectedObject>> {
                override fun onSuccess(detectionObject: List<DetectedObject>?) {
                    if (!detectionObject?.isEmpty()!!) {
                        val builder = StringBuilder()
                        val boxes = ArrayList<BoxWithLabel>()
                        for (objects in detectionObject) {
                            if (!objects.labels.isEmpty()) {
                                val label: String = objects.labels[0].text
                                builder.append(label).append(": ")
                                    .append(objects.labels[0].confidence)
                                    .append("\n")
                                boxes.add(BoxWithLabel(objects.boundingBox, label))
                            } else {
                                builder.append("Unknown").append("\n")
                            }
                        }
                        getOutputTextView().text = builder.toString()
                        drowDetectionResult(boxes, bitmap)
                    } else {
                        getOutputTextView().text = "Could not object detected"

                    }
                }

            })
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(p0: Exception) {
                    TODO("Not yet implemented")
                }
            })
    }


}