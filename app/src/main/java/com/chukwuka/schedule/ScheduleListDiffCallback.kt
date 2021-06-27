package com.chukwuka.schedule

import androidx.recyclerview.widget.DiffUtil
import com.chukwuka.schedule.data.model.Schedule

class ScheduleListDiffCallback(private val oldList: List<Schedule>, private val newList: List<Schedule>) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].id == oldList[oldItemPosition].id
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition] == oldList[oldItemPosition]
    }
}