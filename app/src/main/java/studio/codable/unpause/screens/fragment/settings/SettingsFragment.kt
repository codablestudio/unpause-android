package studio.codable.unpause.screens.fragment.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_settings.*
import studio.codable.unpause.R
import studio.codable.unpause.screens.activity.login.LoginActivity
import studio.codable.unpause.screens.fragment.premium.PremiumFeaturesFragment
import studio.codable.unpause.utilities.manager.GeofencingManager
import studio.codable.unpause.utilities.manager.SessionManager

class SettingsFragment : PremiumFeaturesFragment() {

    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }

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

            change_company_button.text = getTitleForCompanyButton()
            change_company_button.setOnClickListener {
                svm.navigate(SettingsFragmentDirections.actionSettingsFragmentToChangeCompanyFragment())
            }

            //location managing is for users that don't have company
            add_location_button.visibility = if (userVm.userHasConnectedCompany()) View.GONE else View.VISIBLE
            add_location_button.setOnClickListener {
                if (userIsPremium) {
                    svm.navigate(SettingsFragmentDirections.actionSettingsFragmentToLocationsListFragment())
                } else {
                    launchPremiumScreen()
                }

            }

            log_out_button.setOnClickListener {
                dialogManager.openConfirmDialog(R.string.are_you_sure) {
                    userVm.signOut()
                    showMessage(getString(R.string.signed_out))
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
            }


            //don't enable geolocation for non-premium users
            location_based_notifications.visibility = if (userIsPremium) View.VISIBLE else View.GONE
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

    private fun getTitleForCompanyButton(): String = if (userVm.userHasConnectedCompany()) {
        getString(R.string.change_company)
    } else {
        getString(R.string.connect_company)
    }
}
