package com.example.statusbartemp.UpdateWorker

import javax.inject.Singleton

@Singleton
interface TemperatureUpdateInfa {

    suspend fun StartCheckWorker(start : Boolean)

    suspend fun isWorkRunning() : Boolean

}