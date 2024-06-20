package com.capstone.skinenthusiast.di

import com.capstone.skinenthusiast.BuildConfig
import com.capstone.skinenthusiast.data.remote.ApiService
import com.capstone.skinenthusiast.data.repo.MainRepository
import com.capstone.skinenthusiast.ui.auth.login.LoginViewModel
import com.capstone.skinenthusiast.ui.auth.register.RegisterViewModel
import com.capstone.skinenthusiast.ui.camera.CameraViewModel
import com.capstone.skinenthusiast.ui.editprofile.EditProfileViewModel
import com.capstone.skinenthusiast.ui.main.MainViewModel
import com.capstone.skinenthusiast.ui.main.ui.home.HomeViewModel
import com.capstone.skinenthusiast.ui.main.ui.profile.ProfileViewModel
import com.capstone.skinenthusiast.ui.splash.SplashViewModel
import com.capstone.skinenthusiast.utils.SettingsPreferences
import com.capstone.skinenthusiast.utils.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataStoreModules = module {
    single { SettingsPreferences.getInstance(androidContext().dataStore) }
}

val networkModules = module {
    factory {
        val token = runBlocking {
            get<SettingsPreferences>().getToken().first()
        }
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level =
                                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                        }
                    )
                    .addInterceptor { chain ->
                        val request = chain.request()
                        val authenticatedRequest =
                            if (token != SettingsPreferences.PREFERENCE_DEFAULT_VALUE) {
                                request.newBuilder()
                                    .addHeader("Authorization", "Bearer $token")
                                    .build()
                            } else {
                                request
                            }
                        chain.proceed(authenticatedRequest)
                    }
                    .build())
            .build()
            .create(ApiService::class.java)
    }
}

val repositoryModules = module {
    factory { MainRepository(get(), get()) }
}

val viewModelModules = module {
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { CameraViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { EditProfileViewModel(get()) }
}