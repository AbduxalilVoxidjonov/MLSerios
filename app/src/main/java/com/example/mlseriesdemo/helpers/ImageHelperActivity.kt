package com.example.mlseriesdemo.helpers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import com.example.mlseriesdemo.databinding.ActivityImageHelperBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat

abstract class ImageHelperActivity : AppCompatActivity() {

    private var REQUEST_CODE_PICK_IMAGE = 1000
    private var REQUEST_CAPTURE_IMAGE = 1001

    lateinit var binding: ActivityImageHelperBinding


    lateinit var photoFile: File


    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageHelperBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // log
        Log.d("ImageHelperActivity", "onRequestPermissionsResult: ${grantResults[0]}")
    }

    fun onPickImage(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"

        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    fun onStartCamera(view: View) {
        // create file to share with camera
        photoFile = createPhotoFile()
        val fileUri = FileProvider.getUriForFile(this, "com.example.fileprovider", photoFile)
        // create an intent
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        //startactivity for result
        startActivityForResult(intent, REQUEST_CAPTURE_IMAGE)
    }

    fun createPhotoFile(): File {
        val photoFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ML_Image_Helper")
        if (!photoFile.exists()) {
            photoFile.mkdirs()
        }
        val name = SimpleDateFormat("yyyyMMdd_HHmm ss").format(System.currentTimeMillis())
        val file = File(photoFile.path + File.separator + "$name")
        return file
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_IMAGE) {
                val uri = data?.data
                val bitmap: Bitmap = loadFromUri(uri!!)
                binding.ivImage.setImageBitmap(bitmap)
                runClassification(bitmap)

            } else if (requestCode == REQUEST_CAPTURE_IMAGE) {
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                binding.ivImage.setImageBitmap(bitmap)
                runClassification(bitmap)
            }
        }
    }

    fun loadFromUri(uri: Uri): Bitmap {
        var bitmap: Bitmap? = null

        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                val sourse: ImageDecoder.Source = ImageDecoder.createSource(contentResolver, uri)
                bitmap = ImageDecoder.decodeBitmap(sourse)
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap!!
    }


    abstract fun runClassification(bitmap: Bitmap)

    fun getOutputTextView(): TextView {
        return binding.tvOutput
    }

    fun getImageView(): ImageView {
        return binding.ivImage
    }

    fun drowDetectionResult(boxes: List<BoxWithLabel>, bitmap: Bitmap) {
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val penRect = Paint()
        penRect.color = Color.RED
        penRect.style = Paint.Style.STROKE
        penRect.strokeWidth = 8.0f

        val penLabel = Paint()
        penLabel.color = Color.YELLOW
        penLabel.style = Paint.Style.FILL_AND_STROKE
        penLabel.textSize = 80.0f

        for (boxWithlabel in boxes) {
            canvas.drawRect(boxWithlabel.rect, penRect)
            val labelSize = Rect(0, 0, 0, 0)
            penLabel.getTextBounds(boxWithlabel.label, 0, boxWithlabel.label.length, labelSize)

            val frontSize: Float = penLabel.textSize * boxWithlabel.rect.width() / labelSize.width()
            if (frontSize < penLabel.textSize) {
                penLabel.textSize = frontSize
            }

            canvas.drawText(
                boxWithlabel.label,
                boxWithlabel.rect.left.toFloat(),
                boxWithlabel.rect.top.toFloat()+labelSize.height().toFloat(),
                penLabel
            )
        }
        getImageView().setImageBitmap(outputBitmap)
    }

}