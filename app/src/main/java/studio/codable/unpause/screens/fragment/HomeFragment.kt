package studio.codable.unpause.screens.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.android.synthetic.main.fragment_home.*
import studio.codable.unpause.R
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.model.Shift
import studio.codable.unpause.screens.UserViewModel
import studio.codable.unpause.utilities.Constants.Chart.dayLabels
import studio.codable.unpause.utilities.helperFunctions.date
import studio.codable.unpause.utilities.helperFunctions.dayOfWeek
import studio.codable.unpause.utilities.helperFunctions.getCurrentWeek
import studio.codable.unpause.utilities.manager.GeofencingManager
import studio.codable.unpause.utilities.manager.PermissionManager
import studio.codable.unpause.utilities.manager.TimeManager
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class HomeFragment : BaseFragment(false) {

    private val userVm: UserViewModel by activityViewModels()

    @Inject
    lateinit var geofenceManager: GeofencingManager

    @Inject
    lateinit var permissionManager : PermissionManager

    /**
     * isChecked -> user is checked in = state ON
     */
    private val checkInButtonListener: (CompoundButton, Boolean) -> Unit = { _, isChecked ->
        if (isChecked) {
            userVm.checkIn()
            Timber.d("checked in")
        } else {
            userVm.checkOut("test description")
            Timber.d("checked out")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initObservers()
    }

    private fun initUI() {
        btn_check_in_out.setOnCheckedChangeListener(checkInButtonListener)
    }

    private fun initObservers() {
        userVm.errors.observe(viewLifecycleOwner, Observer {
            showError(it.getContentIfNotHandled())
        })

        userVm.user.observe(viewLifecycleOwner, Observer {
            with(it) {
                text_name.text = String.format("%s %s",firstName.orEmpty(),lastName.orEmpty())
                text_company.text = company?.name.orEmpty()
            }
        })

        userVm.shifts.observe(viewLifecycleOwner, Observer {
            Timber.d("Shifts: $it")
            initGraph()
        })

        userVm.geofences.observe(viewLifecycleOwner, Observer { geofences ->
            if (permissionManager.checkFineLocationPermission()) {
                geofences.forEach {
                    geofenceManager.addGeofence(it, true)
                }
            }
            else {
                permissionManager.requestLocationPermission(this)
            }
        })
    }

    @SuppressLint("ResourceType")
    private fun initGraph() {

        val barDataSet = BarDataSet(getWorkingHours(), getString(R.string.working_hours)).apply {
            color = Color.parseColor(getString(R.color.primary))
        }

        val barData = BarData().apply { addDataSet(barDataSet) }

        val xAxisFormatter: ValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return dayLabels[value.toInt()]
            }
        }
        chart.xAxis.apply {
            position = XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f
            labelCount = 7
            valueFormatter = xAxisFormatter
        }

        chart.apply {
            data = barData
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            description.isEnabled = false
            setTouchEnabled(false)
            setDrawGridBackground(false)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            extraBottomOffset = 7f
            animateY(600)
        }
    }

    private fun getWorkingHours(): MutableList<BarEntry> {
        val range = getCurrentWeek()
        val workingHours = filterActivity(userVm.shifts.value!!, range.firstDay, range.lastDay)
            .groupBy({ shift -> shift.arrivalTime.date() },
                {
                    if (it.exitTime == null) {
                        0.000
                    } else {
                        TimeManager(it.arrivalTime!!, it.exitTime!!).getWorkingHoursDecimal()
                    }
                })
            .mapValues { entry ->
                var sum = 0.00
                entry.value.forEach { sum += it }
                sum
            }
            .mapKeys {
                it.key.dayOfWeek()
            }

        val returnList: MutableList<BarEntry> = mutableListOf()
        for (i in 0..6) {
            if (workingHours.containsKey(i)) {
                returnList.add(BarEntry(i.toFloat(), workingHours.getValue(i).toFloat()))
            } else {
                returnList.add(BarEntry(i.toFloat(), 0f))
            }
        }

        return returnList
    }

    private fun filterActivity(shifts: List<Shift>, from: Date, to: Date): ArrayList<Shift> {
        val returnList: ArrayList<Shift> = arrayListOf()
        for (shift in shifts) {
            if (shift.exitTime != null && shift.arrivalTime!! >= from && shift.exitTime!! <= to) {
                returnList.add(shift)
            }
        }
        return returnList
    }
}