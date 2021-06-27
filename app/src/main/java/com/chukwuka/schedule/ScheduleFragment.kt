package com.chukwuka.schedule


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.chukwuka.schedule.viewmodel.ScheduleViewModel
import kotlinx.android.synthetic.main.fragment_schedule.*
import java.text.DateFormatSymbols
import java.util.*


class ScheduleFragment : Fragment() {

    private var currentCalendar: Calendar = Calendar.getInstance()

    var appBarTitle = setupToolbarTitle(
        currentCalendar.get(Calendar.DAY_OF_MONTH),
        currentCalendar.get(Calendar.MONTH),
        currentCalendar.get(Calendar.YEAR)
    )

    lateinit var scheduleViewModel: ScheduleViewModel

    private val schedulesAdapter = SchedulesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleViewModel = ViewModelProvider(requireActivity()).get(ScheduleViewModel::class.java)

        scheduleViewModel.scheduleViewContract.observe(this, Observer {
            it.getContentIfNotHandled()?.let { info ->
                when (info) {
                    is ScheduleViewContract.NavigateToEditSchedule -> {
                        // Put selected schedule to be edited in bundle for addEditFragment
                        val bundle = bundleOf(SCHEDULE to info.schedule, DATE_IN_MILLIS to info.schedule.timeInMillis)

                        // Navigate to add/edit view
                        findNavController(this).navigate(R.id.action_schedule_to_addEditSchedule, bundle)
                    }

                    is ScheduleViewContract.NoSchedules -> {
                        // Show or hide the empty list view
                        if (info.isEmpty) {
                            label_no_schedule.visibility = View.VISIBLE
                        } else {
                            label_no_schedule.visibility = View.GONE
                        }
                    }

                    else -> {
                        // Do nothing
                    }
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Subscribe to data from viewmodel
        scheduleViewModel.schedules.observe(viewLifecycleOwner, Observer {
            schedulesAdapter.updateSchedules(it)
        })

        // Prepare the schedules adapter for item click listening
        schedulesAdapter.listener = { schedule ->
            // Put schedule in bundle for detail view
            val bundle = bundleOf(SCHEDULE to schedule)
            // Navigate to detail view
            ScheduleDetailFragment.newInstance(schedule).show(childFragmentManager, "")
//            findNavController().navigate(R.id.action_schedule_to_scheduleDetail, bundle, null, null)
        }

        // Click listener for the floating action button
        fab.setOnClickListener {
            // Put selected date time in millis in bundle for addEditFragment
            val bundle = bundleOf(DATE_IN_MILLIS to currentCalendar.timeInMillis)

            // Navigate to add/edit view
            findNavController(this).navigate(R.id.action_schedule_to_addEditSchedule, bundle)
        }

        // Set recycler view adapter to order adapter
        recycler_view.adapter = schedulesAdapter

        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.title = appBarTitle
                    isShow = true
                } else if (isShow) {
                    collapsingToolbarLayout.title = " "
                    //careful there should a space between double quote otherwise it wont work
                    isShow = false
                }
            }
        })

        scheduleViewModel.getSchedulesForDate(
            currentCalendar.get(Calendar.DAY_OF_MONTH),
            currentCalendar.get(Calendar.MONTH),
            currentCalendar.get(Calendar.YEAR)
        )

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            if (isDateInThePast(currentDate(dayOfMonth, month, year))) {
                fab.hide()
            } else {
                fab.show()
            }
            setupToolbarTitle(dayOfMonth, month, year)

            scheduleViewModel.getSchedulesForDate(dayOfMonth, month, year)
        }
    }

    private fun setupToolbarTitle(dayOfMonth: Int, month: Int, year: Int): String {
        val calendar = Calendar.getInstance()
        appBarTitle = if (calendar.get(Calendar.YEAR) != year) {
            "${DateFormatSymbols().months[month]} $dayOfMonth, $year"
        } else {
            "${DateFormatSymbols().months[month]} $dayOfMonth"
        }

        return appBarTitle
    }

    private fun currentDate(dayOfMonth: Int, month: Int, year: Int): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)

        // Update current calendar variable
        currentCalendar = calendar

        return calendar
    }

    private fun isDateInThePast(calendar: Calendar): Boolean {

        val reference = Calendar.getInstance()

        if (
            calendar.get(Calendar.DAY_OF_MONTH) == reference.get(Calendar.DAY_OF_MONTH) &&
            calendar.get(Calendar.MONTH) == reference.get(Calendar.MONTH) &&
            calendar.get(Calendar.YEAR) == reference.get(Calendar.YEAR)
        ) {
            // Same day, return false
            return false
        }

        return calendar.timeInMillis < reference.timeInMillis
    }

    companion object {
        const val DATE_IN_MILLIS = "dateInMillis"
        const val SCHEDULE = "schedule"
    }
}
