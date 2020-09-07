package studio.codable.unpause.screens.fragment.userActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_user_activity.*
import studio.codable.unpause.R
import studio.codable.unpause.model.Shift
import studio.codable.unpause.model.User
import studio.codable.unpause.screens.fragment.premium.PremiumFeaturesFragment
import studio.codable.unpause.screens.fragment.workingTimeWarning.WorkingTimeWarningFragment
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.Constants.Chart.MAX_ALLOWED_CHART_TIME_RANGE
import studio.codable.unpause.utilities.adapter.userActivityRecyclerViewAdapter.UserActivityRecyclerViewAdapter
import studio.codable.unpause.utilities.chart.ShiftMarkerView
import studio.codable.unpause.utilities.helperFunctions.*
import studio.codable.unpause.utilities.manager.ChartManager
import studio.codable.unpause.utilities.manager.ChartManager.Companion.getLineChartData
import studio.codable.unpause.utilities.manager.ChartManager.Companion.getLineChartDataset
import studio.codable.unpause.utilities.manager.CsvManager
import studio.codable.unpause.utilities.manager.TimeManager
import studio.codable.unpause.utils.adapters.userActivityRecyclerViewAdapter.SwipeActionCallback
import timber.log.Timber
import java.util.*

class UserActivityFragment : PremiumFeaturesFragment() {

    private lateinit var timeManager: TimeManager
    private lateinit var user : User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = userVm.user.value!!

        initUI()
        initObservers()
    }

    private fun initObservers() {
        userVm.loading.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { shouldShowLoading ->
                if (shouldShowLoading)
                    showLoading()
                else
                    hideLoading()
            }
        })

        //update UI every time there are shift changes
        userVm.user.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            updateUI()
        })
        userVm.shifts.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            updateUI()
        })
    }

    private fun initUI() {

        activity?.let {

            initTimeManager()

            initSpeedDialView()

            from_date_text_view.text = formatFilterDate(timeManager.arrivalTime)
            to_date_text_view.text = formatFilterDate(timeManager.exitTime)

            selected_date.setOnClickListener {
                dialogManager.openDateRangePickerDialog(
                    Calendar.getInstance().apply {
                        time = timeManager.arrivalTime
                        add(Calendar.DATE, 1)
                    }.timeInMillis,
                    Calendar.getInstance().apply { time = timeManager.exitTime }.timeInMillis
                ) { selection ->
                    val calendar1 = Calendar.getInstance()
                    calendar1.timeInMillis = selection.first!!
                    val calendar2 = Calendar.getInstance()
                    calendar2.timeInMillis = selection.second!!
                    timeManager.changeArrivalDate(
                        calendar1.time.year(),
                        calendar1.time.month(),
                        calendar1.time.day()
                    )
                    timeManager.changeExitDate(
                        calendar2.time.year(),
                        calendar2.time.month(),
                        calendar2.time.day()
                    )
                    //update UI
                    updateFromDate(formatFilterDate(timeManager.arrivalTime))
                    updateToDate(formatFilterDate(timeManager.exitTime))
                    updateUI()
                }
            }

            swipe_to_refresh_recycler_view.setOnRefreshListener {
                Timber.i("onRefresh called from SwipeRefreshLayout")
                userVm.getShifts()
            }

        }

        initLineChart()
        initRecyclerView(requireActivity())

    }

    private fun initRecyclerView(activity: FragmentActivity) {
        val recyclerViewAdapter = UserActivityRecyclerViewAdapter(
            requireContext(),
            { shift, _ -> userVm.deleteShift(shift) },
            { shift, _ -> editShift(shift) }
        )


        user_activity_recycler_view?.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
        }

        val itemTouchHelper =
            ItemTouchHelper(
                SwipeActionCallback(
                    recyclerViewAdapter,
                    activity.window.decorView.findViewById(R.id.layout_home_activity)
                )
            )
        itemTouchHelper.attachToRecyclerView(user_activity_recycler_view)
    }

    private fun initLineChart() {

        val lineData = getLineChartDataset(
            arrayListOf(),
            timeManager.arrivalTime.date(),
            timeManager.exitTime.date(),
            requireContext()
        )

        val markerView = ShiftMarkerView(requireContext())
        markerView.chartView = line_chart

        ChartManager.initLineChart(line_chart, lineData, markerView)
    }

    private fun sendCSV() {
        val fileUri =  CsvManager.getCsvFileUri(
                requireContext(),
                filterActivity(),
                getString(R.string.csv_file_name, user.firstName, user.lastName)
        )
        val emailIntent = prepareEmail(fileUri)
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)))
    }

    private fun prepareEmail(fileUri: Uri): Intent {
        val to = arrayOf(user.company?.email.toString())

        return Intent(Intent.ACTION_SEND).apply {
            type = "text/html"
            putExtra(Intent.EXTRA_EMAIL, to)
            putExtra(Intent.EXTRA_STREAM, fileUri)
            putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.email_subject)
            )
            putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.email_body)
            ) }

    }

    private fun openCSV(fileUri: Uri) {
        val csvIntent = Intent(Intent.ACTION_VIEW)

        csvIntent.setDataAndType(fileUri, "text/csv")
        csvIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        csvIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        requireActivity().startActivity(csvIntent)
    }

    private fun initSpeedDialView() {
        speed_dial_view?.inflate(R.menu.menu_speed_dial)

        speed_dial_view?.setOnActionSelectedListener { speedDialActionItem ->
            when (speedDialActionItem.id) {

                R.id.open_csv_button -> {
                    if (userIsPremium) {
                        handleOpenCSVTap()
                    } else {
                        launchPremiumScreen()
                        false
                    }
                }

                R.id.send_as_email_button -> {
                    if (userIsPremium) {
                        sendCSV()
                    } else {
                        launchPremiumScreen()
                    }
                    false
                }

                R.id.add_custom_shift_button -> {
                    handleAddCustomShiftButtonTap()
                }
                else -> false
            }
        }
    }

    private fun handleAddCustomShiftButtonTap(): Boolean {
        return if (userHasExitTime()) {
            showError(getString(R.string.custom_shift_adding_outside_of_working_time_warning))
            true
        } else {
            dialogManager.openWorkingTimeDialog(
                Calendar.getInstance().apply {
                    time = Date()
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }.time,
                Calendar.getInstance().apply {
                    time = Date()
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                }.time,
                true,
                dialogManager,
                object : WorkingTimeWarningFragment.DialogListener {
                    override fun onContinue(
                        arrivalTime: Date,
                        exitTime: Date
                    ) {
                        dialogManager.openDescriptionDialog(
                            getString(R.string.what_did_you_work_on), null, true, { description ->
                                val newShift =
                                    Shift(
                                        arrivalTime,
                                        exitTime,
                                        description
                                    )
                                if (shiftIsOverlapping(newShift)) {
                                    showMessage(getString(R.string.error_cannot_add_new_shift_the_entered_shift_is_overlapping_with_already_existing_shift))
                                } else {
                                    userVm.addCustomShift(newShift)
                                    showMessage(getString(R.string.shift_added))
                                }
                            },
                            {
                                //no action
                            })
                    }
                })
            false
        }
    }

    private fun userHasExitTime() = user.shifts.last().exitTime == null

    private fun handleOpenCSVTap(): Boolean {
        val fileUri = CsvManager.getCsvFileUri(
            requireContext(),
            user.getUserActivity(
                timeManager.arrivalTime,
                timeManager.exitTime
            ),
            getString(R.string.csv_file_name, user.firstName, user.lastName)
        )
        openCSV(fileUri)
        return false
    }


    private fun initTimeManager() {
        val cal1 = Calendar.getInstance()
        cal1.apply {
            add(Calendar.MONTH, -1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val cal2 = Calendar.getInstance()
        cal2.apply {
            time = Date()
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }

        timeManager = TimeManager(cal1.time, cal2.time)
    }

    private fun updateFromDate(activeDate: String) {
        from_date_text_view.text = activeDate
    }

    private fun updateToDate(activeDate: String) {
        to_date_text_view.text = activeDate
    }

    fun editShift(shift: Shift) {
        dialogManager.openWorkingTimeDialog(
            shift.arrivalTime!!,
            shift.exitTime!!,
            false,
            dialogManager,
            object : WorkingTimeWarningFragment.DialogListener {
                override fun onContinue(arrivalTime: Date, exitTime: Date) {
                    dialogManager.openDescriptionDialog(
                        getString(R.string.what_did_you_work_on),
                        shift.description,
                        true,
                        { description: String ->

                            val newShift = Shift(arrivalTime, exitTime, description)

                            if (shiftIsOverlapping(newShift)) {
                                showMessage(getString(R.string.error_cannot_edit_shift_the_edited_shift_is_overlapping_with_already_existing_shift))
                            } else {
                                userVm.editShift(shift, newShift)
                                showMessage(getString(R.string.shift_edited))
                            }

                        },
                        {
                            //no action
                        })

                }
            })
    }

    private fun updateUI() {
        stopSwipeRefreshAnimation()
        updateChart()
        updateRecyclerView()
    }

    private fun stopSwipeRefreshAnimation() {
        if (swipe_to_refresh_recycler_view.isRefreshing)
            swipe_to_refresh_recycler_view.isRefreshing = false
    }

    private fun updateRecyclerView() {
        (user_activity_recycler_view?.adapter as UserActivityRecyclerViewAdapter)
            .updateContent(filterActivity())
        user_activity_recycler_view?.adapter?.notifyDataSetChanged()
    }

    private fun updateChart() {
        val timeRange = timeManager.exitTime.time - timeManager.arrivalTime.time
        if (timeRange > MAX_ALLOWED_CHART_TIME_RANGE) {
            line_chart.visibility = View.GONE
            total_hours_group.visibility = View.VISIBLE
            var sum = 0f
            getLineChartData(
                filterActivity(),
                timeManager.arrivalTime.date(),
                timeManager.exitTime.date()
            ).forEach { sum += it.y }
            text_total_working_hours.text = TimeManager.formatTime(sum)
        } else {
            line_chart.visibility = View.VISIBLE
            total_hours_group.visibility = View.GONE
            line_chart.data = getLineChartDataset(
                filterActivity(),
                timeManager.arrivalTime.date(),
                timeManager.exitTime.date(),
                requireContext()
            )
            line_chart.notifyDataSetChanged()
            line_chart.invalidate()
        }
    }

    private fun filterActivity(): List<Shift> = user.getUserActivity(
        timeManager.arrivalTime,
        timeManager.exitTime
    )

    private fun formatFilterDate(date : Date) : String {
        return getString(R.string.user_activity_filter_date_format,
            Constants.Chart.dayLabels[date.dayOfWeek()!!],
            date.date().toPattern("dd.MM."))
    }

    override fun onResume() {
        super.onResume()
        user.shifts?.let {
            updateUI()
        }
    }

    private fun shiftIsOverlapping(shift: Shift) : Boolean {
        for (s in user.shifts) {
            if (s.isOverlapping(shift.arrivalTime, shift.exitTime))
                return true
        }
        return false
    }
}
