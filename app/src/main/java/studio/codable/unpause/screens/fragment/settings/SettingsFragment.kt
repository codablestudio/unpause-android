package studio.codable.unpause.screens.fragment.settings

import android.app.Activity
import android.content.Context
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
import studio.codable.unpause.model.User
import studio.codable.unpause.screens.UserViewModel
import studio.codable.unpause.screens.activity.login.LoginActivity
import studio.codable.unpause.utilities.manager.DialogManager
import studio.codable.unpause.utilities.manager.GeofencingManager
import studio.codable.unpause.utilities.manager.SessionManager

class SettingsFragment : BaseFragment(false) {


    private val userVm: UserViewModel by activityViewModels()
    private val sessionManager: SessionManager by lazy { SessionManager(requireContext()) }
    private val dialogManager: DialogManager by lazy { DialogManager(activity as BaseActivity) }

    companion object {
        const val ACTION_CHANGE_PERSONAL_INFO = "personal_info"
        const val ACTION_CHANGE_PASSWORD = "password"
    }

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

//    fun onSettingsEvent(action: String) {
//        listener?.onSettingsEvent(action)
//    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is SettingsListener) {
//            listener = context
//        } else {
//            throw RuntimeException("$context must implement SettingsListener")
//        }
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        listener = null
//    }

    interface SettingsListener {
        fun onSettingsEvent(action: String)

    }

    private fun initUI() {

//        activity?.let {
//            change_personal_info.setOnClickListener {
//                onSettingsEvent(ACTION_CHANGE_PERSONAL_INFO)
//            }
//
//            change_password_button.setOnClickListener {
//                onSettingsEvent(ACTION_CHANGE_PASSWORD)
//            }
//
//            log_out_button.setOnClickListener {
//                dialogManager.openConfirmDialog {
//                    userVm.signOut()
//                    showMessage(getString(R.string.signed_out))
//                    val intent = Intent(context, LoginActivity::class.java)
//                    startActivity(intent)
//                    activity?.finish()
//                }
//            }
//
//            location_switch.isChecked = sessionManager.locationServiceStatus
//
//            location_switch.setOnCheckedChangeListener { _, isChecked ->
//                if (isChecked) {
//                    enableLocationService(it)
//                } else {
//                    disableLocationService(it)
//                }
//            }
//        }
    }

    private fun enableLocationService(activity: Activity) {
        sessionManager.locationServiceStatus = true
        val geofencingManager = GeofencingManager(requireContext())
        geofencingManager.reloadSavedGeofences(true)
    }

    private fun disableLocationService(activity: Activity) {
        sessionManager.locationServiceStatus = false
        val geofencingManager = GeofencingManager(requireContext())
        geofencingManager.disableAllGeofences()
    }
}
