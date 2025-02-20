package com.example.statusbartemp.UpdateWorker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MyWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    workerDependency: WorkerDependency
): Worker(context, workerParams) {
    val workerDependency = workerDependency
    override fun doWork(): Result {
        workerDependency.superUpdateTemperature()
        return Result.success()
    }
}


