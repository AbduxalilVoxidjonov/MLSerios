package com.example.mlseriesdemo.Image

import android.graphics.Bitmap
import android.os.Bundle
import com.example.mlseriesdemo.helpers.ImageHelperActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions

class FlowerIdentificationActivity : ImageHelperActivity() {

    lateinit var imageLabeler: ImageLabeler

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val localModel = LocalModel.Builder()
            .setAssetFilePath("model_flowers.tflite")
            .build()

        val options = CustomImageLabelerOptions.Builder(localModel)
            .setConfidenceThreshold(0.7f)
            .setMaxResultCount(5)
            .build()
        imageLabeler = ImageLabeling.getClient(options)
    }

    @Override
    override fun runClassification(bitmap: Bitmap) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        imageLabeler.process(inputImage)
            .addOnSuccessListener { labels ->
                if (labels.size > 0) {
                    val stringbuilder = StringBuilder()
                    for (label in labels) {
                        stringbuilder
                            .append(label.text)
                            .append(" : ")
                            .append(label.confidence)
                            .append("\n")
                    }
                    getOutputTextView().text = stringbuilder.toString()
                } else {
                    getOutputTextView().text = "No labels found"
                }
            }
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    e.printStackTrace()
                }
            })

    }
}