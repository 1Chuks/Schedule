package com.chukwuka.schedule

import com.chukwuka.schedule.data.model.Schedule

sealed class ScheduleViewContract {

    // Contract class to display message to the user
    class MessageDisplay(val message: String) : ScheduleViewContract()

    // Contract class to show empty list view
    class NoSchedules(val isEmpty: Boolean) : ScheduleViewContract()

    // Contract class to edit a schedule
    class NavigateToEditSchedule(val schedule: Schedule) : ScheduleViewContract()

    // Contract object to navigate back to list of schedules
    object SaveSuccess : ScheduleViewContract()
}