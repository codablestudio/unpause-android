package studio.codable.unpause.screens.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import studio.codable.unpause.utilities.manager.SessionManager
import studio.codable.unpause.utilities.manager.TimeManager
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class HomeFragment : PremiumFeaturesFragment() {

    @Inject
    lateinit var geofenceManager: GeofencingManager

    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

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

        home_swipe_refresh.setOnRefreshListener {
            userVm.getUser()
        }

        btn_check_in_out.setOnClickListener{
            userVm.isCheckedIn.value?.let { isCheckedIn ->
                if (!isCheckedIn) {
                    userVm.checkIn()
                    Timber.d("checked in")
                } else {
                    dialogManager.openDescriptionDialog(
                        R.string.what_did_you_work_on,
                        null,
                        true,
                        { description ->
                            userVm.checkOut(description)
                            Timber.d("checked out")
                        },
                        null
                    )
                }
            }
        }

        text_company.setOnClickListener {
            if (userVm.company.value!=null) {
                svm.navigate(HomeFragmentDirections.actionHomeFragmentToDisplayCompanyLocationsFragment())
            } else {
                showMessage(getString(R.string.please_wait_while_the_company_is_loading))
            }
        }

        initGraph()
    }

    private fun initObservers() {
        userVm.loading.observe(viewLifecycleOwner, Observer {
            defaultHandleLoading(it)
        })

        userVm.errors.observe(viewLifecycleOwner, Observer {
            showError(it.getContentIfNotHandled())
        })

        userVm.user.observe(viewLifecycleOwner, Observer {
            with(it) {
                text_name.text = getString(R.string.firstName_lastName, firstName.orEmpty(), lastName.orEmpty())
                text_company.text = getString(R.string.loading_dots)
            }
            stopSwipeRefreshAnimation()
        })

        userVm.company.observe(viewLifecycleOwner, Observer {
            text_company.text = it.name
        })

        userVm.locations.observe(viewLifecycleOwner, Observer {
            text_company.text = getString(R.string.no_company_connected)
        })

        userVm.shifts.observe(viewLifecycleOwner, Observer {
            Timber.d("Shifts: $it")
            if (userVm.isCheckedIn.value != true) {
                updateGraph()
            }
            hideLoading()
        })

        userVm.isCheckedIn.observe(viewLifecycleOwner, Observer {
            refreshCheckInCheckOut(it)
        })

        userVm.checkInCheckOutMessages.observe(viewLifecycleOwner, Observer {
            showMessage(getString(it))
        })

        userVm.geofences.observe(viewLifecycleOwner, Observer { geofences ->
            if (userIsPremium && sessionManager.locationServiceStatus) {
                geofences.forEach {
                    geofenceManager.addGeofence(it, true)
                }
            }
        })

        if (!userIsPremium) {
            geofenceManager.disableAllGeofences()
        }
    }

    private fun refreshCheckInCheckOut(it: Boolean) {
        text_check_in_check_out.text = getCheckInCheckOutText(it)
        handleCheckInDetailsGroup(it)
    }

    private fun getCheckInCheckOutText(isCheckedIn: Boolean): String {
        return if (isCheckedIn) getString(R.string.check_out) else getString(R.string.check_in)
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
            if (shift.arrivalTime != null && shift.exitTime != null && shift.arrivalTime!! >= from && shift.exitTime!! <= to) {
                returnList.add(shift)
            }
        }
        return returnList
    }

    private fun stopSwipeRefreshAnimation() {
        if (home_swipe_refresh.isRefreshing)
            home_swipe_refresh.isRefreshing = false
    }

    private fun handleCheckInDetailsGroup(isGroupVisible : Boolean) {
        with(userVm.shifts.value.active()) {
            if (isGroupVisible && this!=null) {
                val timeManager = TimeManager(this.arrivalTime!!, Calendar.getInstance().time)
                text_last_check_in.text = this.arrivalTime.toPattern("dd.MM.yyyy HH:mm")
                text_working_time.text = getString(R.string.n_h_m_min,timeManager.getWorkingHours().hours,
                    timeManager.getWorkingHours().minutes)
                check_in_details_group.visibility = View.VISIBLE
            } else {
                check_in_details_group.visibility = View.GONE
            }
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