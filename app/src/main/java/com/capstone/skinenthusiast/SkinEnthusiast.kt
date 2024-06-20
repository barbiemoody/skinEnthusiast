package com.capstone.skinenthusiast

import android.app.Application
import com.capstone.skinenthusiast.di.dataStoreModules
import com.capstone.skinenthusiast.di.networkModules
import com.capstone.skinenthusiast.di.repositoryModules
import com.capstone.skinenthusiast.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SkinEnthusiast: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SkinEnthusiast)
            modules(
                dataStoreModules,
                networkModules,
                repositoryModules,
                viewModelModules
            )
        }
    }
}