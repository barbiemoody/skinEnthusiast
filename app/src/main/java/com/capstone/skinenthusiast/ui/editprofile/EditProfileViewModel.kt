package com.capstone.skinenthusiast.ui.editprofile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.skinenthusiast.data.repo.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class EditProfileViewModel(private val repository: MainRepository) : ViewModel() {
    val imageFile = MutableLiveData<File>()

    fun getAvatar() = repository.getAvatar()
    fun getName() = repository.getName()
    fun getEmail() = repository.getEmail()
    fun getGender() = repository.getGender()

    fun updateData(
        gender: String,
        name: String? = null,
        email: String? = null,
        password: String? = null
    ) = repository.updateData(name, email, password, gender)

    fun updateData(
        image: File,
        gender: String,
        name: String? = null,
        email: String? = null,
        password: String? = null
    ) = repository.updateDataWithImage(image,name, email, password, gender)

    fun saveAccountData(
        name: String,
        email: String,
        gender: String,
        avatar: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveAccountData(name, email, gender, avatar)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearPreferences()
        }
    }
}