package com.capstone.skinenthusiast.ui.auth.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.skinenthusiast.data.repo.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: MainRepository
) : ViewModel() {
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String>()

    fun login(email: String, password: String) =
        repository.login(email, password)

    fun saveToken(
        token: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveToken(token)
        }
    }

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

    fun getToken() = repository.getToken().asLiveData()
}