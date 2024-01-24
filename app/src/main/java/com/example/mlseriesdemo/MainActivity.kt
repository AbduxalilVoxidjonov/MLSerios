package com.example.mlseriesdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.mlseriesdemo.Image.FlowerIdentificationActivity
import com.example.mlseriesdemo.Image.ImageClassificationActivity
import com.example.mlseriesdemo.Image.ObjectDetectionActivity
import com.example.mlseriesdemo.databinding.ActivityMainBinding
import com.example.mlseriesdemo.helpers.ImageHelperActivity

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    fun onGotoImageActivity(view: View){
        val intent = Intent(this@MainActivity, ImageClassificationActivity::class.java)
        startActivity(intent)


    }
    fun onGotoFlowerIdentification(view: View){
        val intent = Intent(this@MainActivity, FlowerIdentificationActivity::class.java)
        startActivity(intent)
    }
    fun onGotoObjectDetection(view: View){
        val intent = Intent(this@MainActivity, ObjectDetectionActivity::class.java)
        startActivity(intent)
    }
}