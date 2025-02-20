package com.example.statusbartemp.AlarmManager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.INTENT_ACTION
import com.example.statusbartemp.UpdateWorker.WorkerDependency
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/*
Android13: We have full control when we want to ask the user for permission
Android 12L or lower: The system will show the permission dialog when the app creates its first notification channel*/


@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject lateinit var workerDependency : WorkerDependency
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == INTENT_ACTION){
            workerDependency.initializeTemperatureUpdater()
        }
    }
}
