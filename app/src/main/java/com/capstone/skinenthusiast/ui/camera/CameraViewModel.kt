package com.capstone.skinenthusiast.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.skinenthusiast.data.repo.MainRepository
import kotlinx.coroutines.launch
import java.io.File

class CameraViewModel(private val repository: MainRepository) : ViewModel() {
    fun predictSkin(image: File) = repository.predictSkin(image)

    fun logout() {
        viewModelScope.launch {
            repository.clearPreferences()
        }
    }
}