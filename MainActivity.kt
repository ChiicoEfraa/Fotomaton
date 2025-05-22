
package com.example.fototvprint

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var countdownText: TextView
    private lateinit var burpPlayer: MediaPlayer
    private lateinit var btnFoto: Button
    private lateinit var btnGaleria: Button
    private lateinit var btnSmartView: Button
    private var currentPhoto: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        countdownText = findViewById(R.id.countdownText)
        btnFoto = findViewById(R.id.btnFoto)
        btnGaleria = findViewById(R.id.btnGaleria)
        btnSmartView = findViewById(R.id.btnSmartView)

        burpPlayer = MediaPlayer.create(this, R.raw.burp)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)

        btnFoto.setOnClickListener { startCountdown() }
        btnGaleria.setOnClickListener {
            startActivity(Intent(this, GalleryActivity::class.java))
        }
        btnSmartView.setOnClickListener {
            startActivity(Intent(android.provider.Settings.ACTION_CAST_SETTINGS))
        }
    }

    private fun startCountdown() {
        object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownText.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                countdownText.text = ""
                openCamera()
            }
        }.start()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            val bitmap = data?.extras?.get("data") as Bitmap
            currentPhoto = bitmap
            imageView.setImageBitmap(bitmap)
            burpPlayer.start()
            saveImage(bitmap)
        }
    }

    private fun saveImage(bitmap: Bitmap) {
        val folder = File(filesDir, "photos")
        if (!folder.exists()) folder.mkdirs()
        val file = File(folder, "${System.currentTimeMillis()}.jpg")
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
    }
}
