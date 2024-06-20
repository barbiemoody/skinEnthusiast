package com.capstone.skinenthusiast.ui.main.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.skinenthusiast.data.repo.MainRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: MainRepository) : ViewModel() {
    fun getAvatar() = repository.getAvatar()
    fun getName() = repository.getName()
    fun getEmail() = repository.getEmail()

    fun logout() {
        viewModelScope.launch {
            repository.clearPreferences()
        }
    }
}