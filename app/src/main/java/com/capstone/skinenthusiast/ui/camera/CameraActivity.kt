package com.capstone.skinenthusiast.ui.camera

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.capstone.skinenthusiast.R
import com.capstone.skinenthusiast.databinding.ActivityCameraBinding
import com.capstone.skinenthusiast.ui.scanresult.ScanResultActivity
import com.capstone.skinenthusiast.utils.Constants
import com.capstone.skinenthusiast.utils.Result
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class CameraActivity : AppCompatActivity() {
    private val viewModel by viewModel<CameraViewModel>()

    private lateinit var binding: ActivityCameraBinding

    private var imageCapture: ImageCapture? = null
    private var cameraPosition: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflaterLoad: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(this)
            .setView(inflaterLoad.inflate(R.layout.custom_loader, null))
            .setCancelable(true)
        loadingDialog = loadingAlert.create()

        enableEdgeToEdge()
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startCameraService()
        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            btnSwitch.setOnClickListener {
                cameraPosition =
                    if (cameraPosition == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                    else CameraSelector.DEFAULT_BACK_CAMERA
                startCameraService()
            }
            btnShutter.setOnClickListener {
                takePhoto()
            }
        }
    }

    private fun startCameraService() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(720, 1280))
                .build()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = binding.viewFinder.surfaceProvider
                }
            imageCapture = ImageCapture.Builder().setTargetResolution(Size(720, 1280)).build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraPosition,
                    preview,
                    imageCapture, imageAnalysis
                )
            } catch (e: Exception) {
                showToast("Error : ${e.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val capturedImageFile = createFile(application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(capturedImageFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(e: ImageCaptureException) {
                    showToast("Error : ${e.message}")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val reducedImageFile = reduceImageSize(capturedImageFile)

                    viewModel.predictSkin(reducedImageFile).observe(this@CameraActivity) { result ->
                        when (result) {
                            is Result.Loading -> {
                                loadingDialog.show()
                            }

                            is Result.Success -> {
                                loadingDialog.dismiss()
                                val iResult =
                                    Intent(this@CameraActivity, ScanResultActivity::class.java)

                                iResult.putExtra(EXTRA_IMAGE, capturedImageFile)
                                iResult.putExtra(
                                    EXTRA_CAMERA_MODE,
                                    cameraPosition == CameraSelector.DEFAULT_BACK_CAMERA
                                )
                                iResult.putExtra(EXTRA_RESULT, result.data.data)

                                startActivity(iResult)
                                this@CameraActivity.finish()
                            }

                            is Result.Error -> {
                                loadingDialog.dismiss()

                                if (result.error.contains("401")) {
                                    Constants.showUnauthorized(this@CameraActivity) {
                                        viewModel.logout()
                                    }
                                } else {
                                    showToast(result.error)
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    private fun createFile(application: Application): File {
        val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
            File(it, "image").apply { mkdirs() }
        }

        return File(
            if (
                mediaDir != null && mediaDir.exists()
            ) mediaDir else application.filesDir, "${
                SimpleDateFormat(
                    "ddMMyySSSSS",
                    Locale.getDefault()
                ).format(System.currentTimeMillis())
            }.jpg"
        )
    }

    private fun reduceImageSize(file: File): File {
        val maxFileSize = 1000000
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var outputStream: FileOutputStream? = null

        try {
            while (compressQuality > 0) {
                val bmpStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                val bmpPicByteArray = bmpStream.toByteArray()

                if (bmpPicByteArray.size <= maxFileSize) {
                    outputStream = FileOutputStream(file)
                    outputStream.write(bmpPicByteArray)
                    outputStream.flush()
                    break
                }

                compressQuality -= 5
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            outputStream?.close()
        }

        return file
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_CAMERA_MODE = "extra_camera_mode"
        const val EXTRA_RESULT = "extra_result"
    }
}