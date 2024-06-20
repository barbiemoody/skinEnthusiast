package com.capstone.skinenthusiast.ui.splash

import androidx.lifecycle.ViewModel
import com.capstone.skinenthusiast.data.repo.MainRepository
import com.capstone.skinenthusiast.utils.SettingsPreferences

class SplashViewModel(private val repository: MainRepository): ViewModel() {
    fun getToken() = repository.getToken()
}