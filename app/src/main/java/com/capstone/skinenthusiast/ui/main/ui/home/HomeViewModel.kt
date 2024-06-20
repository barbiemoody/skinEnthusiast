package com.capstone.skinenthusiast.ui.main.ui.home

import androidx.lifecycle.ViewModel
import com.capstone.skinenthusiast.data.repo.MainRepository

class HomeViewModel(private val repository: MainRepository) : ViewModel() {
    fun getName() = repository.getName()
}