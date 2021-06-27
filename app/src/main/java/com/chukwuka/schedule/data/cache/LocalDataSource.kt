package com.chukwuka.schedule.data.cache

import androidx.lifecycle.LiveData
import com.chukwuka.schedule.data.DataSource
import com.chukwuka.schedule.data.model.Schedule


class LocalDataSource(private val appDAO: AppDAO) : DataSource {

    override suspend fun getSchedules(): LiveData<List<Schedule>> {
        return appDAO.getSchedules()
    }

    override suspend fun getActiveSchedules(currentTimeInMillis: Long): List<Schedule> {
        return appDAO.getActiveSchedules(currentTimeInMillis)
    }

    override suspend fun getDateSchedules(dayOfMonth: Int, month: Int, year: Int): LiveData<List<Schedule>> {
        return appDAO.getDateSchedules(dayOfMonth, month, year)
    }

    override suspend fun getSchedule(id: Int): Schedule {
        return appDAO.getSchedule(id)
    }

    override suspend fun saveSchedule(schedule: Schedule): Long {
        return appDAO.saveSchedule(schedule)
    }

    override suspend fun deleteSchedule(schedule: Schedule) {
        appDAO.deleteSchedule(schedule)
    }

}