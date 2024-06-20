package com.capstone.skinenthusiast.ui.editprofile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.capstone.skinenthusiast.R
import com.capstone.skinenthusiast.databinding.ActivityEditProfileBinding
import com.capstone.skinenthusiast.utils.Constants
import com.capstone.skinenthusiast.utils.Result
import com.capstone.skinenthusiast.utils.SettingsPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class EditProfileActivity : AppCompatActivity() {
    private val viewModel by viewModel<EditProfileViewModel>()
    private lateinit var imagePathLocation: String

    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var loadingDialog: AlertDialog

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                viewModel.imageFile.postValue(reduceImageSize(uriFileConverter(it)))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflaterLoad: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(this)
            .setView(inflaterLoad.inflate(R.layout.custom_loader, null))
            .setCancelable(true)
        loadingDialog = loadingAlert.create()

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeImage()

        setInitialContent()
        setListeners()
    }

    private fun setInitialContent() {
        binding.apply {
            Glide.with(root.context)
                .load(runBlocking { viewModel.getAvatar().first() })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivProfile)

            edEmail.setText(runBlocking { viewModel.getEmail().first() })
            edName.setText(runBlocking { viewModel.getName().first() })

            val gender = runBlocking { viewModel.getGender().first() }.lowercase()

            if (gender == rbMale.text.toString().lowercase()) {
                rgGender.check(R.id.rb_male)
            } else {
                rgGender.check(R.id.rb_female)
            }
        }
    }

    private fun observeImage() {
        viewModel.imageFile.observe(this) {
            binding.ivProfile.setImageBitmap(BitmapFactory.decodeFile(it.path))
        }
    }

    private fun setListeners() {
        binding.apply {
            btnAddPhoto.setOnClickListener {
                galleryLauncher.launch("image/*")
            }

            btnBack.setOnClickListener {
                finish()
            }

            btnSave.setOnClickListener {
                val emailTemp = runBlocking { viewModel.getEmail().first().toString() }
                val nameTemp = runBlocking { viewModel.getName().first().toString() }

                val email = if (binding.edEmail.text?.takeIf { it.isNotEmpty() }?.toString()
                        ?.lowercase() == emailTemp.lowercase()
                ) {
                    null
                } else {
                    binding.edEmail.text?.takeIf { it.isNotEmpty() }?.toString()
                }

                val name = if (binding.edName.text?.takeIf { it.isNotEmpty() }?.toString()
                        ?.lowercase() == nameTemp.lowercase()
                ) {
                    null
                } else {
                    binding.edName.text?.takeIf { it.isNotEmpty() }?.toString()
                }

                val password = binding.edPassword.text?.takeIf { it.isNotEmpty() }?.toString()
                val gender = if (rgGender.checkedRadioButtonId == binding.rbMale.id) {
                    binding.rbMale.text.toString().uppercase()
                } else {
                    binding.rbFemale.text.toString().uppercase()
                }

                if (viewModel.imageFile.value == null) {
                    viewModel.updateData(gender, name, email, password)
                        .observe(this@EditProfileActivity) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    loadingDialog.show()
                                }

                                is Result.Success -> {
                                    loadingDialog.dismiss()

                                    result.data.data.let {
                                        viewModel.saveAccountData(
                                            it.name ?: SettingsPreferences.PREFERENCE_DEFAULT_VALUE,
                                            it.email
                                                ?: SettingsPreferences.PREFERENCE_DEFAULT_VALUE,
                                            it.gender
                                                ?: SettingsPreferences.PREFERENCE_DEFAULT_VALUE,
                                            it.imageUrl
                                                ?: SettingsPreferences.PREFERENCE_DEFAULT_VALUE
                                        )
                                    }

                                    finish()
                                }

                                is Result.Error -> {
                                    loadingDialog.dismiss()

                                    if (result.error.contains("401")) {
                                        Constants.showUnauthorized(this@EditProfileActivity) {
                                            viewModel.logout()
                                        }
                                    } else {
                                        showToast(result.error)
                                    }
                                }
                            }
                        }
                } else {
                    viewModel.updateData(
                        viewModel.imageFile.value ?: File(""),
                        gender,
                        name,
                        email,
                        password
                    )
                        .observe(this@EditProfileActivity) { result ->
                            when (result) {
                                is Result.Loading -> {
                                    loadingDialog.show()
                                }

                                is Result.Success -> {
                                    loadingDialog.dismiss()

                                    result.data.data.let {
                                        viewModel.saveAccountData(
                                            it.name ?: SettingsPreferences.PREFERENCE_DEFAULT_VALUE,
                                            it.email
                                                ?: SettingsPreferences.PREFERENCE_DEFAULT_VALUE,
                                            it.gender
                                                ?: SettingsPreferences.PREFERENCE_DEFAULT_VALUE,
                                            it.imageUrl
                                                ?: SettingsPreferences.PREFERENCE_DEFAULT_VALUE
                                        )
                                    }

                                    finish()
                                }

                                is Result.Error -> {
                                    loadingDialog.dismiss()

                                    if (result.error.contains("401")) {
                                        Constants.showUnauthorized(this@EditProfileActivity) {
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
        }
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

    private fun uriFileConverter(uri: Uri): File {
        val tempFile = File.createTempFile(
            SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(System.currentTimeMillis()),
            ".jpg",
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        ).also { imagePathLocation = it.absolutePath }

        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return tempFile
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}