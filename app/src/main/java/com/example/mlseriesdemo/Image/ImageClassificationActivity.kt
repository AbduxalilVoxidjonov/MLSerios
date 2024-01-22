package com.example.mlseriesdemo.Image

import android.graphics.Bitmap
import android.os.Bundle
import com.example.mlseriesdemo.helpers.ImageHelperActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions


open class ImageClassificationActivity : ImageHelperActivity() {
    lateinit var imageLabelar: ImageLabeler

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageLabelar = ImageLabeling.getClient(
            ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.7f)
                .build()
        )
    }


    override fun runClassification(bitmap: Bitmap) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        imageLabelar.process(inputImage)
            .addOnSuccessListener(object : OnSuccessListener<List<ImageLabel>> {
                override fun onSuccess(labels: List<ImageLabel>) {
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
            })
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    e.printStackTrace()
                }
            })

    }


}