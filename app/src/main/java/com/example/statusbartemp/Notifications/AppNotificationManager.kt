package com.example.statusbartemp.Notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationCompat
import com.example.statusbartemp.LogicAndData.Constants.Companion.ecmwfForeCastSurfacePointSimpleQuery
import com.example.statusbartemp.LogicAndData.Constants.Companion.fmiForecastEditedPointSimpleQuery
import com.example.statusbartemp.LogicAndData.Constants.Companion.harmonieSurfacePointSimpleQuery
import com.example.statusbartemp.LogicAndData.Constants.Companion.mepsSurfacePointSimpleQuery
import com.example.statusbartemp.LogicAndData.TimeFunctions.Companion.formatISOTimeToFinnishTime
import com.example.statusbartemp.LogicAndData.formatTempToDisplay
import javax.inject.Inject

class AppNotificationManager(context : Context) : AppNotificationInfa{
    val context = context
    val channelId = "temperature_channel"
    val channelName = "Status Bar Temperature"
    
    val errorChannelId = "error_channel"
    val errorChannelName = "Status Bar Temperature Error"
    val notificationManager =
        context
            .getSystemService(
                ComponentActivity.NOTIFICATION_SERVICE
            ) as NotificationManager



    override fun sendNotification(
        iconFilePath : String,
        temperature : Double,
        time : String,
        distance : Float,
        mode : String
    ){
        if (notificationManager != null) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            //Log.v("notificatonmanager", "channel val created")
            notificationManager.createNotificationChannel(channel)
            //Log.v("notificatonmanager", "channel created in mngr")

            // Build the notification
            val icon = context.resources.getIdentifier(iconFilePath, "drawable", context.packageName)
            val timeToDisp = formatISOTimeToFinnishTime(time).take(5)
            val tempToDisplay = formatTempToDisplay(temperature)
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                //.setLargeIcon(icon)
                .setSilent(true)
                .setContentTitle("Current Temperature")
                .setContentText("$tempToDisplay°C    $timeToDisp")
                .setSmallIcon(icon)

            //Log.v("notificatonmanager", "notification built")

            // Show the notification
            val notificationId = 1 // Unique ID for the notification
            notificationManager.notify(notificationId, notificationBuilder.build())
            clearErrorNotification()
            //Log.v("notificatonmanager", "notification sent")
        }
    }
    
    override fun sendErrorNotification(time: String, mode : String) {
        if (notificationManager != null) {
            val channel = NotificationChannel(
                errorChannelId,
                errorChannelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            //Log.v("notificatonmanager", "channel val created")
            notificationManager.createNotificationChannel(channel)
            //Log.v("notificatonmanager", "channel created in mngr")
            
            val modeText = when(mode){
                harmonieSurfacePointSimpleQuery -> "Harmonie"
                mepsSurfacePointSimpleQuery -> "Harmonie (meps)"
                ecmwfForeCastSurfacePointSimpleQuery -> "ECMWF"
                fmiForecastEditedPointSimpleQuery -> "FMI Forecast"
                else -> "FMI"
            }
            // Step 2: Build the notification
            val icon = context.resources.getIdentifier("sbterror3", "drawable", context.packageName)
            //val timeToDisp = formatISOTimeToFinnishTime(time).take(5)
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                //.setLargeIcon(icon)
                .setSilent(true)
                .setContentTitle("Status Bar Temperature Error")
                .setContentText("Failed to obtain temperature from source \'$modeText\' : last attempt at $time")
                .setSmallIcon(icon)
            
            //Log.v("notificatonmanager", "notification built")
            
            // Step 3: Show the notification
            val notificationId = 2 // Unique ID for the notification
            notificationManager.notify(notificationId, notificationBuilder.build())
            //Log.v("notificatonmanager", "notification sent")
        }
    }
    
    fun clearErrorNotification(){
        notificationManager.cancel(2)
    }
}

class AppNotification @Inject constructor(
    private val mngr : AppNotificationInfa
){
    fun createAppNotificationManager() : AppNotificationInfa{
        return mngr
    }

}