package com.example.statusbartemp.Notifications

import javax.inject.Singleton

@Singleton
interface AppNotificationInfa {
    fun sendNotification(
        iconFilePath : String,
        temperature : Double,
        time : String,
        distance : Float,
        mode : String
    )
    
    fun sendErrorNotification(
        time : String = "",
        mode : String = ""
    )


}