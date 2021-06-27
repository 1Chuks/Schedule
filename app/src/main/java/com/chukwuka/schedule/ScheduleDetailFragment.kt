package com.chukwuka.schedule

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.chukwuka.schedule.data.model.Schedule
import com.chukwuka.schedule.util.RoundedBottomSheetDialogFragment
import com.chukwuka.schedule.viewmodel.ScheduleViewModel
import kotlinx.android.synthetic.main.fragment_schedule_detail.*
import java.util.*

class ScheduleDetailFragment : RoundedBottomSheetDialogFragment() {

    lateinit var scheduleViewModel: ScheduleViewModel

    lateinit var schedule: Schedule

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        scheduleViewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)

//        val safeArgs: ScheduleDetailFragmentArgs by navArgs()
//        schedule = safeArgs.schedule

        return inflater.inflate(R.layout.fragment_schedule_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        calendar.timeInMillis = schedule.timeInMillis

        label_time.text = DateUtils.getRelativeTimeSpanString(calendar.timeInMillis, now, DateUtils.DAY_IN_MILLIS)

        if (calendar.timeInMillis < now) {
            button_edit.visibility = View.GONE
        } else {
            button_edit.visibility = View.VISIBLE
        }

//        if (calendar.timeInMillis < now) {
//            layout_container.setBackgroundColor(ContextCompat.getColor(context!!, R.color.gray))
//            if (Build.VERSION.SDK_INT >= 21) {
//                dialog?.window?.navigationBarColor = ContextCompat.getColor(context!!, R.color.gray)
//            }
//        } else {
//            layout_container.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
//            if (Build.VERSION.SDK_INT >= 21) {
//                dialog?.window?.navigationBarColor = ContextCompat.getColor(context!!, R.color.colorPrimary)
//            }
//        }

        label_description.text = schedule.description

        button_edit.setOnClickListener {
            scheduleViewModel.editSchedule(schedule)
            dismiss()
        }

        button_delete.setOnClickListener {
            scheduleViewModel.deleteSchedule(schedule)
            dismiss()
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(schedule: Schedule) = ScheduleDetailFragment().apply { this.schedule = schedule }
    }
}