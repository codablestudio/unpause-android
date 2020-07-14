package studio.codable.unpause.screens.fragment.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_settings.*
import studio.codable.unpause.R
import studio.codable.unpause.base.activity.BaseActivity
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.screens.UserViewModel
import studio.codable.unpause.screens.activity.login.LoginActivity
import studio.codable.unpause.utilities.manager.DialogManager
import studio.codable.unpause.utilities.manager.GeofencingManager
import studio.codable.unpause.utilities.manager.SessionManager

class SettingsFragment : BaseFragment(false) {


    private val userVm: UserViewModel by activityViewModels()
    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }
    private val dialogManager: DialogManager by lazy { DialogManager(activity as BaseActivity) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    private fun initUI() {

        activity?.let {
            change_personal_info.setOnClickListener {
                svm.navigate(SettingsFragmentDirections.actionSettingsFragmentToChangePersonalInfoFragment())
            }

            change_password_button.setOnClickListener {
                svm.navigate(SettingsFragmentDirections.actionSettingsFragmentToChangeUserPasswordFragment())
            }

            change_company_button.setOnClickListener {
                svm.navigate(SettingsFragmentDirections.actionSettingsFragmentToChangeCompanyFragment())
            }

            log_out_button.setOnClickListener {
                dialogManager.openConfirmDialog {
                    userVm.signOut()
                    showMessage(getString(R.string.signed_out))
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
            }

            location_switch.isChecked = sessionManager.locationServiceStatus

            location_switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    enableLocationService()
                } else {
                    disableLocationService()
                }
            }
        }
    }

    private fun enableLocationService() {
        sessionManager.locationServiceStatus = true
        val geofencingManager = GeofencingManager(requireContext())
        userVm.geofences.value?.let { geofencingManager.reloadGeofences(it) }
    }

    private fun disableLocationService() {
        sessionManager.locationServiceStatus = false
        val geofencingManager = GeofencingManager(requireContext())
        geofencingManager.disableAllGeofences()
    }
}
