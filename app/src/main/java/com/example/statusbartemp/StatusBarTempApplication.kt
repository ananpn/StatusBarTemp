package com.example.statusbartemp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class StatusBarTempApplication : Application(), Configuration.Provider {


    @Inject lateinit var hiltWorkerFactory: HiltWorkerFactory

    override val workManagerConfiguration
        get () = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()


}