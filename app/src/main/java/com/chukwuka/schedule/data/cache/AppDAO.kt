package com.chukwuka.schedule.data.cache

import androidx.lifecycle.LiveData
import androidx.room.*
import com.chukwuka.schedule.data.model.Schedule

@Dao
interface AppDAO {

    @Query("SELECT * FROM Schedules ORDER BY timeInMillis")
    fun getSchedules(): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedules WHERE timeInMillis > :currentTimeInMillis ORDER BY timeInMillis")
    suspend fun getActiveSchedules(currentTimeInMillis: Long): List<Schedule>

    @Query("SELECT * FROM Schedules WHERE dayOfMonth = :dayOfMonth AND month = :month AND year = :year ORDER BY timeInMillis")
    fun getDateSchedules(dayOfMonth: Int, month: Int, year: Int): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedules WHERE id = :id")
    fun getSchedule(id: Int): Schedule

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSchedule(schedule: Schedule): Long

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)
}