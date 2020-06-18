package studio.codable.unpause.screens.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_home.*
import studio.codable.unpause.R
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.screens.UserViewModel
import studio.codable.unpause.utilities.manager.GeofencingManager
import studio.codable.unpause.utilities.manager.PermissionManager
import timber.log.Timber

class HomeFragment : BaseFragment(true) {

    private val userVm: UserViewModel by activityViewModels()

    private val geofenceManager: GeofencingManager by lazy {
        GeofencingManager.getInstance(requireContext())
    }

    private val permissionManager by lazy { PermissionManager(requireContext()) }

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
                text_email.text = email
                text_first_name.text = firstName.orEmpty()
                text_last_name.text = lastName.orEmpty()
                text_company.text = company?.name.orEmpty()
            }
        })

        userVm.shifts.observe(viewLifecycleOwner, Observer {
            Timber.d("Shifts: $it")
        })

        userVm.geofences.observe(viewLifecycleOwner, Observer { geofences ->
            if (permissionManager.checkFineLocationPermission()) {
                geofences.forEach {
                    geofenceManager.addGeofence(it, true)
                }
            }
            else {
                permissionManager.shouldShowFineLocationPermissionExplanation(this)
                permissionManager.requestFineLocationPermission(this)
            }
        })
    }

}