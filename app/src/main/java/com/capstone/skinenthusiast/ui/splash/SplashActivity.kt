package com.capstone.skinenthusiast.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.asLiveData
import com.capstone.skinenthusiast.R
import com.capstone.skinenthusiast.databinding.ActivitySplashBinding
import com.capstone.skinenthusiast.ui.auth.AuthActivity
import com.capstone.skinenthusiast.ui.main.MainActivity
import com.capstone.skinenthusiast.utils.SettingsPreferences
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {
    private val viewModel by inject<SplashViewModel>()

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()
    }

    private fun observeViewModel() {
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.getToken().asLiveData().observe(this@SplashActivity) {
                if (it != SettingsPreferences.PREFERENCE_DEFAULT_VALUE) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
                }
                finish()
            }
        }, 1500L)
    }
}