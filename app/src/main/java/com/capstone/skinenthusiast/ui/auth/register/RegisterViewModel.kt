package com.capstone.skinenthusiast.ui.auth.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.skinenthusiast.data.repo.MainRepository

class RegisterViewModel(private val repository: MainRepository) :
    ViewModel() {
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String>()

    fun register(name: String, email: String, password: String, gender: String) =
        repository.register(name, email, password, gender)
}