package com.example.statusbartemp.UpdateWorker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit


class TemperatureUpdateManager(
    context : Context,
) : TemperatureUpdateInfa{
    val context = context
    val workId = "update_temperature_StatusBarTemp"
    val checkWorkId = "check_work_StatusBarTemp"
    
    //checking is done through WorkManager for redundancy
    override suspend fun StartCheckWorker(start : Boolean) {
        if (start) {
            val request = PeriodicWorkRequestBuilder<CheckWorker>(4, TimeUnit.HOURS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                checkWorkId,
                ExistingPeriodicWorkPolicy.REPLACE,
                request
            )
        }
        else {
            WorkManager.getInstance(context).cancelUniqueWork(checkWorkId)
        }
    }



    override suspend fun isWorkRunning() : Boolean {
        val wm = WorkManager.getInstance(context)
        val workInfos = wm.getWorkInfosForUniqueWorkFlow(workId).firstOrNull()
        val output = workInfos?.get(0)?.state?.isFinished ?:workInfos.isNullOrEmpty()
        return !output
    }

}



/*
class MyWorker(
    val vm : MainViewModel,
    val context: Context,
    val params : WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        vm.superUpdateTemperature()
        return Result.success()
    }
}
*/
