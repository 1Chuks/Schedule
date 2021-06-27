package com.chukwuka.schedule

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.chukwuka.schedule.data.ScheduleRepositoryImpl
import com.chukwuka.schedule.data.cache.AppDatabase
import com.chukwuka.schedule.data.cache.LocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext


class AlarmReceiver : BroadcastReceiver(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        val repository =
            ScheduleRepositoryImpl(LocalDataSource(AppDatabase.getInstance(context.applicationContext).appDao()))

        if (intent.action != null) {
            if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

                val alarmManager = AlarmManager(context)

                launch {

                    val schedules = async(Dispatchers.IO) {
                        repository.getActiveSchedules(Calendar.getInstance().timeInMillis)
                    }.await()

                    schedules.forEach {
                        // Re-register the alarms
                        alarmManager.registerAlarm(it)
                    }
                }

                return
            }
        }

        // Alarm received, fetch the schedule and trigger the notification

        launch {
            val schedule = async(Dispatchers.IO) {
                repository.getSchedule(intent.getIntExtra("id", 1))
            }.await()

            showNotification(
                context,
                MainActivity::class.java,
                "Schedule",
                schedule.description,
                schedule.id
            )
        }
    }

    private fun showNotification(context: Context, cls: Class<*>, title: String, content: String, requestCode: Int) {
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val channelId = "schedule_channel_$requestCode"

        val notificationIntent = Intent(context, cls)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(cls)
        stackBuilder.addNextIntent(notificationIntent)

        val pendingIntent =
            stackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, channelId)

        val notification = builder.setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setSound(alarmSound)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(requestCode, notification)
    }
}
