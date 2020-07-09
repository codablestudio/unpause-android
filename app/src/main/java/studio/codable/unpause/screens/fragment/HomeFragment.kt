package studio.codable.unpause.screens.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.android.synthetic.main.fragment_home.*
import studio.codable.unpause.R
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.model.Shift
import studio.codable.unpause.screens.UserViewModel
import studio.codable.unpause.utilities.helperFunctions.date
import studio.codable.unpause.utilities.manager.GeofencingManager
import studio.codable.unpause.utilities.manager.PermissionManager
import studio.codable.unpause.utilities.manager.TimeManager
import timber.log.Timber
import java.time.Month
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

    fun initGraph() {

        getWorkingHours()

        val hours = mutableListOf<BarEntry>().apply {
            add(BarEntry(0f, 3f))
            add(BarEntry(1f, 4f))
            add(BarEntry(2f, 5f))
            add(BarEntry(3f, 6f))
            add(BarEntry(4f, 10f))
            add(BarEntry(5f, 0f))
            add(BarEntry(6f, 1f))
        }
        val barDataSet = BarDataSet(hours, "Working hours").apply {
            color = R.color.primary
        }

        val barData = BarData().apply { addDataSet(barDataSet) }

        val xAxisFormatter: ValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when(value) {
                    0f -> "Mon"
                    1f -> "Tue"
                    2f -> "Wed"
                    3f -> "Thu"
                    4f -> "Fri"
                    5f -> "Sat"
                    6f -> "Sun"
                    else -> ""
                }
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

    //    private fun getWorkingHours() : MutableList<BarEntry> {
    private fun getWorkingHours() : Unit {

        //TODO: make this return a list with 7 elements according to days
        val workingHours = filterActivity(userVm.shifts.value!!,
            Calendar.getInstance().apply {
                add(Calendar.DATE, -7)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.time,
            Calendar.getInstance().apply {
                time = Date()
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }.time
        ).groupBy({shift -> shift.arrivalTime.date()},
            {
                if (it.exitTime == null ) {
                    0.000
                } else {
                    TimeManager(it.arrivalTime!!, it.exitTime!!).getWorkingHoursDecimal()
                }
            })
            .mapValues { entry ->
            var sum = 0.00
            entry.value.forEach { sum+=it }
            sum
        }
        Timber.i(workingHours.toString())
    }

    private fun filterActivity(shifts: List<Shift>,from: Date, to: Date): ArrayList<Shift> {
        val returnList: ArrayList<Shift> = arrayListOf()
        for (shift in shifts) {
            if (shift.exitTime != null && shift.arrivalTime!! >= from && shift.exitTime!! <= to) {
                returnList.add(shift)
            }
        }
        return returnList
    }


}