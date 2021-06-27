package com.chukwuka.schedule.viewmodel

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chukwuka.schedule.AlarmManager
import com.chukwuka.schedule.ScheduleViewContract
import com.chukwuka.schedule.data.ScheduleRepository
import com.chukwuka.schedule.data.model.Schedule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

class ScheduleViewModel(private val repository: ScheduleRepository, context: Context) : ViewModel(), CoroutineScope {

    private val alarmManager = AlarmManager(context)

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    // LiveData object for view state interaction
    val scheduleViewContract: MutableLiveData<Event<ScheduleViewContract>> = MutableLiveData()

    // Mediator livedata that sits between the database retrieved livedata object and the observing view
    val schedules = MediatorLiveData<List<Schedule>>()

    fun getSchedulesForDate(dayOfMonth: Int, month: Int, year: Int) {

        val job = async {
            repository.getDateSchedules(dayOfMonth, month, year)
        }

        launch(Dispatchers.Main) {
            // Subscribe to repository fetched schedules
            schedules.addSource(job.await()) {
                schedules.postValue(it)

                if (it.isEmpty()) {
                    scheduleViewContract.postValue(Event(ScheduleViewContract.NoSchedules(true)))
                } else {
                    scheduleViewContract.postValue(Event(ScheduleViewContract.NoSchedules(false)))
                }
            }
        }
    }

    fun saveSchedule(description: String, calendar: Calendar) {
        calendar.set(Calendar.SECOND, 0)

        val schedule = Schedule(
            description = description,
            timeInMillis = calendar.timeInMillis,
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
            month = calendar.get(Calendar.MONTH),
            year = calendar.get(Calendar.YEAR)
        )

        launch {
            val id = repository.saveSchedule(schedule)

            if (id != 0L) {
                schedule.apply {
                    this.id = id.toInt()
                }
                alarmManager.registerAlarm(schedule)
                // Successfully saved. return to schedules
                scheduleViewContract.postValue(Event(ScheduleViewContract.SaveSuccess))
            } else {
                // Saved failed, show message
                scheduleViewContract.postValue(Event(ScheduleViewContract.MessageDisplay("Failed to save schedule")))
            }
        }
    }

    fun updateSchedule(schedule: Schedule, description: String, calendar: Calendar) {
        calendar.set(Calendar.SECOND, 0)

        schedule.apply {
            this.description = description
            timeInMillis = calendar.timeInMillis
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
        }

        launch {
            val id = repository.saveSchedule(schedule)

            if (id != 0L) {
                alarmManager.registerAlarm(schedule)
                // Successfully saved. return to schedules
                scheduleViewContract.postValue(Event(ScheduleViewContract.SaveSuccess))
            } else {
                // Saved failed, show message
                scheduleViewContract.postValue(Event(ScheduleViewContract.MessageDisplay("Failed to save schedule")))
            }
        }
    }

    fun editSchedule(schedule: Schedule) {
        // Navigate to edit fragment
        scheduleViewContract.postValue(Event(ScheduleViewContract.NavigateToEditSchedule(schedule)))
    }

    fun deleteSchedule(schedule: Schedule) {
        launch {
            repository.deleteSchedule(schedule)
            alarmManager.cancelAlarm(schedule)
        }
    }
}