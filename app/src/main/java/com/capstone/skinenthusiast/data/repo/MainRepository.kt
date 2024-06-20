package com.capstone.skinenthusiast.data.repo

import androidx.lifecycle.liveData
import com.capstone.skinenthusiast.data.remote.ApiService
import com.capstone.skinenthusiast.utils.SettingsPreferences
import com.capstone.skinenthusiast.utils.Result
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class MainRepository(
    private val apiService: ApiService,
    private val preferences: SettingsPreferences
) {
    fun login(email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val loginResponse = apiService.login(email, password)
            android.util.Log.e("FTEST", "cek token -> ${loginResponse.token}")
            val accountResponse = apiService.getAccountData("Bearer ${loginResponse.token}")
            accountResponse.user?.token = loginResponse.token
            emit(Result.Success(accountResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        gender: String,
    ) = liveData {
        emit(Result.Loading)
        try {
            val registerResponse = apiService.register(name, email, password, gender)
            emit(Result.Success(registerResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    fun predictSkin(
        image: File
    ) = liveData {
        emit(Result.Loading)
        try {
            val uploadResponse = apiService.predictSkin(
                MultipartBody.Part.createFormData(
                    "image",
                    image.name,
                    image.asRequestBody("image/jpeg".toMediaTypeOrNull())
                ),
            )
            emit(Result.Success(uploadResponse))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun updateDataWithImage(
        image: File,
        name: String? = null,
        email: String? = null,
        password: String? = null,
        gender: String,
    ) = liveData {
        emit(Result.Loading)
        try {
            try {
                apiService.updateProfileImage(
                    MultipartBody.Part.createFormData(
                        "image",
                        image.name,
                        image.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    ),
                )
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.Error(e.message.toString()))
            }

            val registerResponse = apiService.updateData(name, email, password, gender)
            emit(Result.Success(registerResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    fun updateData(
        name: String? = null,
        email: String? = null,
        password: String? = null,
        gender: String,
    ) = liveData {
        emit(Result.Loading)
        try {
            val registerResponse = apiService.updateData(name, email, password, gender)
            emit(Result.Success(registerResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun clearPreferences() = preferences.clearPreferences()
    suspend fun saveToken(token: String) = preferences.saveToken(token)

    suspend fun saveAccountData(
        name: String,
        email: String,
        gender: String,
        avatar: String,
    ) = preferences.saveAccountData(name, email, gender, avatar)

    fun getToken() = preferences.getToken()
    fun getName() = preferences.getName()
    fun getEmail() = preferences.getEmail()
    fun getGender() = preferences.getGender()
    fun getAvatar() = preferences.getAvatar()
}