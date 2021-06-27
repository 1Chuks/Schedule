package com.chukwuka.schedule

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import com.chukwuka.schedule.data.model.Schedule


class AlarmManager(val context: Context) {

    fun registerAlarm(schedule: Schedule) {
        // Enable a receiver

        val receiver = ComponentName(context, AlarmReceiver::class.java)
        val pm = context.packageManager

        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("id", schedule.id)

        val pendingIntent =
            PendingIntent.getBroadcast(context, schedule.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val am = context.getSystemService(ALARM_SERVICE) as AlarmManager
        am.setExact(
            AlarmManager.RTC_WAKEUP,
            schedule.timeInMillis,
            pendingIntent
        )
    }

    fun cancelAlarm(schedule: Schedule) {
        // Disable a receiver

        val receiver = ComponentName(context, AlarmReceiver::class.java)
        val pm = context.packageManager

        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, schedule.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val am = context.getSystemService(ALARM_SERVICE) as AlarmManager
        am.cancel(pendingIntent)
        pendingIntent.cancel()
    }

}