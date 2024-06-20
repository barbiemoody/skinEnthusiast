package com.capstone.skinenthusiast.ui.main

import androidx.lifecycle.ViewModel
import com.capstone.skinenthusiast.data.repo.MainRepository

class MainViewModel(private val repository: MainRepository): ViewModel() {
    fun getName() = repository.getName()
}