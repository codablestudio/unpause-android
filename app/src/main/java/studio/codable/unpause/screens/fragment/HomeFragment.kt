package studio.codable.unpause.screens.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_home.*
import studio.codable.unpause.R
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.model.Shift
import studio.codable.unpause.screens.UserViewModel
import studio.codable.unpause.utilities.helperFunctions.getCurrentWeek
import studio.codable.unpause.utilities.manager.ChartManager.Companion.getBarChartDataset
import studio.codable.unpause.utilities.manager.ChartManager.Companion.initBarChart
import studio.codable.unpause.utilities.manager.GeofencingManager
import studio.codable.unpause.utilities.manager.PermissionManager
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
                text_name.text = getString(R.string.firstName_lastName, firstName.orEmpty(), lastName.orEmpty())
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

    private fun initGraph() {

        val range = getCurrentWeek()
        val workingHours = filterActivity(userVm.shifts.value!!, range.firstDate, range.lastDate)

        initBarChart(chart, getBarChartDataset(workingHours, requireContext()))
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