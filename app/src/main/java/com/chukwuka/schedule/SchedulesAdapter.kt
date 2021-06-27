package com.chukwuka.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chukwuka.schedule.data.model.Schedule
import kotlinx.android.synthetic.main.item_schedule_action.view.*
import java.util.*
import kotlin.collections.ArrayList

class SchedulesAdapter : RecyclerView.Adapter<SchedulesAdapter.ViewHolder>() {

    private var schedules: List<Schedule> = ArrayList()

    var listener: (Schedule) -> Unit = { }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_schedule_action,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(schedules[position])
    }

    override fun getItemCount(): Int {
        return schedules.size
    }

    fun updateSchedules(items: List<Schedule>) {
        // Update list of schedules
        val diffResult = DiffUtil.calculateDiff(ScheduleListDiffCallback(schedules, items))
        schedules = items
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(schedule: Schedule) {
            val calendar = Calendar.getInstance()

            if (schedule.timeInMillis < calendar.timeInMillis) {
                itemView.card.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.gray))
            } else {
                itemView.card.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorPrimary))
            }

            calendar.timeInMillis = schedule.timeInMillis

            when {
                calendar.get(Calendar.HOUR_OF_DAY) == 0 -> {
                    itemView.label_hour.text = String.format("%02d", 12)
                    itemView.label_meridian.text = itemView.context.getString(R.string.period_am)
                }

                calendar.get(Calendar.HOUR_OF_DAY) > 12 -> {
                    itemView.label_hour.text = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY) - 12)
                    itemView.label_meridian.text = itemView.context.getString(R.string.period_pm)
                }

                calendar.get(Calendar.HOUR_OF_DAY) < 12 -> {
                    itemView.label_hour.text = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))
                    itemView.label_meridian.text = itemView.context.getString(R.string.period_am)
                }

                else -> {
                    itemView.label_hour.text = String.format("%02d", 12)
                    itemView.label_meridian.text = itemView.context.getString(R.string.period_pm)
                }
            }

            itemView.label_minute.text = String.format("%02d", calendar.get(Calendar.MINUTE))

            itemView.label_action.text = schedule.description

            itemView.setOnClickListener {
                listener(schedule)
            }
        }
    }
}