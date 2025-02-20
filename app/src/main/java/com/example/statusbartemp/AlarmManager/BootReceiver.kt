package com.example.statusbartemp.AlarmManager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.statusbartemp.UpdateWorker.WorkerDependency
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject lateinit var workerDependency : WorkerDependency

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            workerDependency.initializeTemperatureUpdater()
        }
    }
}
