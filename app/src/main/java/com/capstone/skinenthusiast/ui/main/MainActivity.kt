package com.capstone.skinenthusiast.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.capstone.skinenthusiast.R
import com.capstone.skinenthusiast.databinding.ActivityMainBinding
import com.capstone.skinenthusiast.utils.Result
import com.capstone.skinenthusiast.utils.SettingsPreferences
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModel<MainViewModel>()

    private lateinit var binding: ActivityMainBinding

//    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val inflaterLoad: LayoutInflater = layoutInflater
//        val loadingAlert = AlertDialog.Builder(this)
//            .setView(inflaterLoad.inflate(R.layout.custom_loader, null))
//            .setCancelable(true)
//        loadingDialog = loadingAlert.create()
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

//        observeViewModel()
    }

//    private fun observeViewModel() {
//        viewModel.getName().asLiveData().observe(this@MainActivity) { name ->
//            if (name == SettingsPreferences.PREFERENCE_DEFAULT_VALUE) {
//                getAccountData {
//
//                }
//            }
//        }
//    }
//
//    private fun getAccountData(onRetry: () -> Unit) {
//        viewModel.getAccountData().observe(this@MainActivity) { result ->
//            when (result) {
//                is Result.Loading -> {
//                    loadingDialog.show()
//                }
//
//                is Result.Success -> {
//                    loadingDialog.dismiss()
//                }
//
//                is Result.Error -> {
//                    loadingDialog.dismiss()
//                    onRetry.invoke()
//                }
//            }
//        }
//    }


}