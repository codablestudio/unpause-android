package studio.codable.unpause.screens.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_home.*
import studio.codable.unpause.R
import studio.codable.unpause.model.Shift
import studio.codable.unpause.screens.fragment.premium.PremiumFeaturesFragment
import studio.codable.unpause.utilities.extensions.active
import studio.codable.unpause.utilities.helperFunctions.getCurrentWeek
import studio.codable.unpause.utilities.helperFunctions.toPattern
import studio.codable.unpause.utilities.manager.ChartManager.Companion.getBarChartDataset
import studio.codable.unpause.utilities.manager.ChartManager.Companion.initBarChart
import studio.codable.unpause.utilities.manager.GeofencingManager
import studio.codable.unpause.utilities.manager.PermissionManager
import studio.codable.unpause.utilities.manager.TimeManager
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class HomeFragment : PremiumFeaturesFragment() {

    @Inject
    lateinit var geofenceManager: GeofencingManager

    @Inject
    lateinit var permissionManager : PermissionManager

    /**
     * isChecked -> user is checked in = state ON
     */
    private val checkInButtonListener: (CompoundButton, Boolean) -> Unit = { button, isChecked ->
       if (button.isPressed) {
           if (isChecked) {
               userVm.checkIn()
               Timber.d("checked in")
           } else {
               dialogManager.openDescriptionDialog(R.string.what_did_you_work_on, null, true, {
                   userVm.checkOut(it)
                   Timber.d("checked out")
               }, null)
           }
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

        showLoading()
        initUI()
        initObservers()
    }

    private fun initUI() {
        btn_check_in_out.isChecked = userVm.isCheckedIn.value ?: false
        btn_check_in_out.setOnCheckedChangeListener(checkInButtonListener)

        initGraph()
    }

    private fun initObservers() {
        userVm.errors.observe(viewLifecycleOwner, Observer {
            showError(it.getContentIfNotHandled())
        })

        userVm.user.observe(viewLifecycleOwner, Observer {
            with(it) {
                text_name.text = getString(R.string.firstName_lastName, firstName.orEmpty(), lastName.orEmpty())
                text_company.text = getString(R.string.loading_dots)
            }
        })

        userVm.company.observe(viewLifecycleOwner, Observer {
            text_company.text = it.name
        })

        userVm.locations.observe(viewLifecycleOwner, Observer {
            text_company.text = getString(R.string.no_company_connected)
        })

        userVm.shifts.observe(viewLifecycleOwner, Observer {
            Timber.d("Shifts: $it")
            hideLoading()
            if (userVm.isCheckedIn.value != true) {
                updateGraph()
            }
            handleCheckInDetailsGroup(userVm.isCheckedIn.value ?: false)
        })

        userVm.isCheckedIn.observe(viewLifecycleOwner, Observer {
            btn_check_in_out.isChecked = it
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

        if (!userIsPremium) {
            geofenceManager.disableAllGeofences()
        }
    }

    private fun initGraph() {
        initBarChart(chart, getBarChartDataset(arrayListOf(), requireContext()))
    }

    private fun updateGraph() {
        val range = getCurrentWeek()
        val workingHours =
            filterActivity(userVm.shifts.value!!, range.firstDate, range.lastDate)

        chart.data = getBarChartDataset(workingHours, requireContext())
        chart.notifyDataSetChanged()
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

    private fun handleCheckInDetailsGroup(isGroupVisible : Boolean) {
        if (isGroupVisible) {
            val timeManager = TimeManager(userVm.shifts.value.active()!!.arrivalTime!!, Calendar.getInstance().time)
            text_last_check_in.text = userVm.shifts.value.active()?.arrivalTime.toPattern("dd.MM.yyyy HH:mm")
            text_working_time.text = getString(R.string.n_h_m_min,timeManager.getWorkingHours().hours,
                timeManager.getWorkingHours().minutes)
            check_in_details_group.visibility = View.VISIBLE
        } else {
            check_in_details_group.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        userVm.getShifts()
        userVm.shifts.value?.let {
            updateGraph()
        }
        userVm.user.value?.let {
            if (it.companyId != null)
                userVm.getCompany(it.companyId)
        }

    }
}