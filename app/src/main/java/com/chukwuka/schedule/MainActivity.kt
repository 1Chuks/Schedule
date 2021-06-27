package com.chukwuka.schedule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.chukwuka.schedule.data.ScheduleRepositoryImpl
import com.chukwuka.schedule.data.cache.AppDatabase
import com.chukwuka.schedule.data.cache.LocalDataSource
import com.chukwuka.schedule.viewmodel.ScheduleViewModel
import com.chukwuka.schedule.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Create view model with activity scope so that hosted fragment will have access to the same instance
        ViewModelProvider(
            this, ViewModelFactory(
                ScheduleRepositoryImpl(LocalDataSource(AppDatabase.getInstance(applicationContext).appDao())),
                applicationContext
            )
        ).get(ScheduleViewModel::class.java)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
