package com.capstone.skinenthusiast.ui.scanresult

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.capstone.skinenthusiast.data.models.Data
import com.capstone.skinenthusiast.databinding.ActivityScanResultBinding
import com.capstone.skinenthusiast.ui.camera.CameraActivity
import java.io.File
import java.io.FileOutputStream

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class ScanResultActivity : AppCompatActivity() {
    private val imageFile by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(
                CameraActivity.EXTRA_IMAGE,
                File::class.java
            )
        } else {
            intent.getSerializableExtra(
                CameraActivity.EXTRA_IMAGE
            )
        }
    }
    private val isBackCamera by lazy { intent.getSerializableExtra(CameraActivity.EXTRA_CAMERA_MODE) }
    private val scanResult by lazy {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(CameraActivity.EXTRA_RESULT)
        } else {
            intent.getParcelableExtra(CameraActivity.EXTRA_RESULT, Data::class.java)
        }
    }

    private lateinit var binding: ActivityScanResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        setContents()
    }

    private fun setContents() {
        binding.apply {
            ivImage.setImageBitmap(BitmapFactory.decodeFile((imageFile as File).path))

            scanResult?.let { scanData ->
                tvSkinType.text = StringBuilder("Skin Type : ${scanData.result}")

                if (scanData.result.lowercase() == "normal") {
                    layoutContent.visibility = View.GONE
                } else {
                    tvSkincareName.text = scanData.nama
                    tvIngredients.text = scanData.bahan
                    tvBenefit.text = scanData.benefit
                    tvUsage.text = scanData.usage

                    layoutContent.visibility = View.VISIBLE
                }
            } ?: run {
                layoutContent.visibility = View.GONE
            }
        }
    }

    private fun rotateFile(file: File, isBackCamera: Boolean = false) {
        val matrix = Matrix()
        val bitmap = BitmapFactory.decodeFile(file.path)
        val rotation = if (isBackCamera) 90f else -90f
        matrix.postRotate(rotation)
        if (!isBackCamera) {
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        }
        val result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
    }
}