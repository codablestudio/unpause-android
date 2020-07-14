package studio.codable.unpause.screens.fragment.userActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.rangeTo
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_user_activity.*
import studio.codable.unpause.R
import studio.codable.unpause.base.activity.BaseActivity
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.model.Shift
import studio.codable.unpause.model.User
import studio.codable.unpause.screens.UserViewModel
import studio.codable.unpause.screens.fragment.workingTimeWarning.WorkingTimeWarningFragment
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.Constants.Chart.MAX_ALLOWED_CHART_TIME_RANGE
import studio.codable.unpause.utilities.adapter.userActivityRecyclerViewAdapter.UserActivityRecyclerViewAdapter
import studio.codable.unpause.utilities.chart.ShiftMarkerView
import studio.codable.unpause.utilities.helperFunctions.*
import studio.codable.unpause.utilities.manager.CsvManager
import studio.codable.unpause.utilities.manager.DialogManager
import studio.codable.unpause.utilities.manager.TimeManager
import studio.codable.unpause.utils.adapters.userActivityRecyclerViewAdapter.SwipeActionCallback
import timber.log.Timber
import java.util.*

class UserActivityFragment : BaseFragment(false) {

    private val userVm: UserViewModel by activityViewModels()

    private val mDialogManager: DialogManager by lazy { DialogManager(activity as BaseActivity) }
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

    }

    private fun initUI() {

        activity?.let {

            initTimeManager()

            initSpeedDialView()

            from_date_text_view.text = timeManager.arrivalToArray()[1]
            to_date_text_view.text = timeManager.exitToArray()[1]

            selected_date.setOnClickListener {
                mDialogManager.openDateRangePickerDialog{ selection ->
                    val calendar1 = Calendar.getInstance()
                    calendar1.timeInMillis = selection.first!!
                    val calendar2 = Calendar.getInstance()
                    calendar2.timeInMillis = selection.second!!
                    timeManager.changeArrivalDate(calendar1.time.year(), calendar1.time.month(),calendar1.time.day())
                    timeManager.changeExitDate(calendar2.time.year(), calendar2.time.month(),calendar2.time.day())
                    //update UI
                    updateFromDate(timeManager.arrivalToArray()[1])
                    updateToDate(timeManager.exitToArray()[1])
                    updateUI()
                }
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

        //update UI every time there are shift changes
        userVm.user.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            updateUI()
        })
        userVm.shifts.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            updateUI()
        })

        updateRecyclerView()

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

        val lineData = getLineChartDataset()

        line_chart.apply {
            data = lineData
            description.isEnabled = false
            setDrawGridBackground(false)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            extraBottomOffset = 3f
            animateY(600)
            marker = ShiftMarkerView(requireContext(), R.layout.shift_marker_view_layout)
            setPinchZoom(false)
            isNestedScrollingEnabled = false
            isHorizontalScrollBarEnabled = false
            isDoubleTapToZoomEnabled = false
        }
    }

    @SuppressLint("ResourceType")
    private fun getLineChartDataset(): LineData {
        val lineDataSet = LineDataSet(getChartData(), getString(R.string.working_hours)).apply {
            color = Color.parseColor(getString(R.color.primary))
            setDrawFilled(true)
            setDrawCircleHole(false)
            setDrawHighlightIndicators(false)
        }

        val lineData = LineData().apply { addDataSet(lineDataSet) }
        line_chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawLabels(false)
        }
        return lineData
    }

    private fun getChartData(): MutableList<Entry> {
        val returnList: MutableList<Entry> = mutableListOf()
                val workingTimes = filterActivity()
            .groupBy({ shift -> shift.arrivalTime.date() })
                    .toSortedMap()
                    .mapValues{
                        var sum = 0.000
                        it.value.forEach {
                            if (it.exitTime != null) {
                                sum += TimeManager(it.arrivalTime!!, it.exitTime!!).getWorkingHoursDecimal()
                            }
                        }
                        sum
                    }
        var index = 0
        TimeManager.getDatesBetween(timeManager.arrivalTime.date(),timeManager.exitTime.date()).forEach {
            if (workingTimes.containsKey(it)) {
                returnList.add(Entry((index++).toFloat(), workingTimes[it]!!.toFloat()).apply {
                    data = it
                })
                } else {
                returnList.add(Entry((index++).toFloat(), 0f).apply {
                    data = it
                })
            }
        }
        return returnList
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
                    val fileUri = CsvManager.getCsvFileUri(
                        requireContext(),
                        user.getUserActivity(
                            timeManager.arrivalTime,
                            timeManager.exitTime
                        ),
                        getString(R.string.csv_file_name, user.firstName, user.lastName)
                    )
                    openCSV(fileUri)
                    false
                }

                R.id.send_as_email_button -> {

                    sendCSV()
                    false
                }

                R.id.add_custom_shift_button -> {
                    if (user.shifts?.last()?.exitTime == null) {
                        showError(getString(R.string.custom_shift_adding_outside_of_working_time_warning))
                        true
                    } else {
                        mDialogManager?.openWorkingTimeDialog(
                            timeManager.arrivalTime,
                            timeManager.exitTime,
                            true,
                            mDialogManager!!,
                            object : WorkingTimeWarningFragment.DialogListener {
                                override fun onContinue(
                                    arrivalTime: Date,
                                    exitTime: Date
                                ) {
                                    mDialogManager?.openDescriptionDialog(
                                        null, { description ->
                                            val newShift =
                                                Shift(
                                                    arrivalTime,
                                                    exitTime,
                                                    description
                                                )
                                            userVm.addCustomShift(newShift)
//                                            updateRecyclerView()
                                            showMessage(getString(R.string.shift_added))
                                        },
                                        {
                                            //no action
                                        })
                                }
                            })
                        false
                    }
                }
                else -> false
            }
        }
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

    fun updateFromDate(activeDate: String) {
        from_date_text_view.text = activeDate
    }

    fun updateToDate(activeDate: String) {
        to_date_text_view.text = activeDate
    }

    fun editShift(shift: Shift) {
        mDialogManager.openWorkingTimeDialog(
                shift.arrivalTime!!,
                shift.exitTime!!,
                false,
                mDialogManager,
                object : WorkingTimeWarningFragment.DialogListener {
                    override fun onContinue(arrivalTime: Date, exitTime: Date) {
                        mDialogManager.openDescriptionDialog(
                                shift.description, { description: String ->

                            val newShift = Shift(arrivalTime, exitTime, description)
                            userVm.editShift(newShift)
                            //                            updateRecyclerView()
                            showMessage(getString(R.string.shift_edited))

                        },
                                {
                                    //no action
                                })

                    }
                })
    }

    private fun updateUI() {
        updateChart()
        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        (user_activity_recycler_view?.adapter as UserActivityRecyclerViewAdapter)
            .updateContent(filterActivity())
        user_activity_recycler_view?.adapter?.notifyDataSetChanged()
    }

    private fun updateChart() {
        val timeRange = timeManager.exitTime.time-timeManager.arrivalTime.time
        if (timeRange > MAX_ALLOWED_CHART_TIME_RANGE) {
            line_chart.visibility = View.GONE
            total_working_hours.visibility = View.VISIBLE
            var sum = 0f
            getChartData().forEach{ sum += it.y }
            text_total_working_hours.text = TimeManager.formatTime(sum)
        } else {
            line_chart.visibility = View.VISIBLE
            total_working_hours.visibility = View.GONE
            line_chart.data = getLineChartDataset()
            line_chart.notifyDataSetChanged()
            line_chart.invalidate()
        }
    }

    private fun filterActivity() : List<Shift> = user.getUserActivity(
        timeManager.arrivalTime,
        timeManager.exitTime
    )

}
