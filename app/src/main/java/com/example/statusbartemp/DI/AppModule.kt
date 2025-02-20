package com.example.statusbartemp.DI

import android.content.Context
import androidx.work.WorkManager
import com.example.statusbartemp.APIstuff.WeatherApi
import com.example.statusbartemp.Location.AppLocationInfa
import com.example.statusbartemp.Location.AppLocationManager
import com.example.statusbartemp.Notifications.AppNotificationInfa
import com.example.statusbartemp.Notifications.AppNotificationManager
import com.example.statusbartemp.Prefs.AppPrefs
import com.example.statusbartemp.Prefs.PrefsImpl
import com.example.statusbartemp.UpdateWorker.TemperatureUpdateInfa
import com.example.statusbartemp.UpdateWorker.TemperatureUpdateManager
import com.example.statusbartemp.UpdateWorker.WorkerDependency
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context : Context): WorkManager {
        return WorkManager.getInstance(context)
}

    @Provides
    @Singleton
    fun provideWorkerDependency(
        locatMan : AppLocationInfa,
        notifMan : AppNotificationInfa,
        prefs: PrefsImpl,
        weatherApi : WeatherApi,
        tempUpdateMan : TemperatureUpdateInfa,
        @ApplicationContext context : Context
    ): WorkerDependency {
        return WorkerDependency(
            locatMan,
            notifMan,
            prefs,
            weatherApi,
            tempUpdateMan,
            context,
        )
    }


    @Provides
    @Singleton
    fun provideApi(): WeatherApi {
        return WeatherApi()
    }

    @Provides
    fun provideAppLocationManager(@ApplicationContext context: Context): AppLocationInfa {
        return AppLocationManager(context)
    }

    @Provides
    fun provideAppNotificationManager(@ApplicationContext context: Context): AppNotificationInfa {
        return AppNotificationManager(context)
    }

    @Singleton
    @Provides
    fun providePreferenceManager(@ApplicationContext context: Context): PrefsImpl {
        return AppPrefs(context)
    }

    @Provides
    fun provideTemperatureUpdateManager(@ApplicationContext context: Context): TemperatureUpdateInfa {
        return TemperatureUpdateManager(context = context)
    }




}