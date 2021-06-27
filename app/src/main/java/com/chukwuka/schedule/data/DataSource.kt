package com.chukwuka.schedule.data

import androidx.lifecycle.LiveData
import com.chukwuka.schedule.data.model.Schedule

/**
 * Interface defining methods for the data storage and retrieval operations related to Schedule.
 * This is to be implemented by external data source layers (Remote and Cache), setting the requirements for the
 * operations that need to be implemented.
 */
interface DataSource {

    /**
     * Gets all the registered schedules from the local database and the remote database
     */
    suspend fun getSchedules(): LiveData<List<Schedule>>

    /**
     * Gets an unobservable list of all the registered schedules from the local database and the remote database
     */
    suspend fun getActiveSchedules(currentTimeInMillis: Long): List<Schedule>

    /**
     * Gets all the schedules registered for a particular date from the local database and the remote database
     */
    suspend fun getDateSchedules(dayOfMonth: Int, month: Int, year: Int): LiveData<List<Schedule>>

    /**
     * Gets the schedule with the id passed from the local database and the remote database
     */
    suspend fun getSchedule(id: Int): Schedule

    /**
     * Saves a schedule to the local database
     */
    suspend fun saveSchedule(schedule: Schedule): Long

    /**
     * Deletes a schedule from the local database
     */
    suspend fun deleteSchedule(schedule: Schedule)
}