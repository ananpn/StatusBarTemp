package com.example.statusbartemp.AlarmManager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_NO_CREATE
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.INTENT_ACTION
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.INTENT_MESSAGE
import com.example.statusbartemp.LogicAndData.StringConstants.Companion.INTENT_NAME

fun scheduleAlarm(context: Context, interval : String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    intent.setAction(INTENT_ACTION)
    intent.putExtra(INTENT_NAME, INTENT_MESSAGE)
    val pendingIntent =
        //API 31
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast( // every pending intent must be unique to show different notifications.
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
            )
        }
        else {
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    //Destroy any existing alarms
    alarmManager?.cancel(pendingIntent)
    //Elapsed time since boot
    //We use ELAPSED_REALTIME_WAKEUP alarm type since this is most reliable
    val millisTime = SystemClock.elapsedRealtime()
    var alarmTime: Long = millisTime-1
    when (interval){
        "Disabled" -> pendingIntent.cancel()
        //"15 minutes" -> alarmTime += 15*1000+1
        "15 minutes" -> alarmTime += 15*60*1000+1
        "30 minutes" -> alarmTime += 30*60*1000+1
        "1 hour" -> alarmTime += 60*60*1000+1
    }

    if (interval != "Disabled") {
        alarmManager?.setWindow(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            alarmTime,//calendar.timeInMillis,
            10*60*1000,
            pendingIntent
        )
    }
}


@SuppressLint("UnspecifiedImmutableFlag")
fun isAlarmSet(context: Context): Boolean {
    val intent = Intent(context, AlarmReceiver::class.java)
    intent.setAction(INTENT_ACTION)
    intent.putExtra(INTENT_NAME, INTENT_MESSAGE)
    var isBackupServiceAlarmSet : Boolean
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) //API 31
    {
        isBackupServiceAlarmSet =
            (PendingIntent.getBroadcast(
                context,
                0,
                intent,
                FLAG_IMMUTABLE or FLAG_NO_CREATE
            ) != null)
    }
    else {
        /*
        PendingIntent.getBroadcast(
            context.getApplicationContext(),
            0,
            intent,
            FLAG_NO_CREATE
        )
        */
        isBackupServiceAlarmSet =
            (PendingIntent.getBroadcast(
                context,
                0,
                intent,
                FLAG_NO_CREATE
            ) != null)
    }
    return isBackupServiceAlarmSet
}