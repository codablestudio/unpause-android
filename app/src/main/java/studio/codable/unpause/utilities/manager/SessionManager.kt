package studio.codable.unpause.utilities.manager

import android.content.Context
import android.content.SharedPreferences
import studio.codable.unpause.model.Shift
import javax.inject.Inject

class SessionManager @Inject constructor(context: Context) {

    companion object {
        private const val USER_ID = "user_id"
        private const val CHECK_IN_STATE = "check_in_state"
        private const val LOCATION_SERVICE_STATUS = "location_service_status"
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences("Session", Context.MODE_PRIVATE)

    var userId: String
        get() = preferences.getString(USER_ID, "").orEmpty()
        set(value) = preferences.edit().putString(USER_ID, value).apply()

    var isCheckedIn: Boolean
        get() = preferences.getBoolean(CHECK_IN_STATE, false)
        set(value) = preferences.edit().putBoolean(CHECK_IN_STATE, value).apply()

    var locationServiceStatus: Boolean
        get() = preferences.getBoolean(LOCATION_SERVICE_STATUS, true)
        set(value) {
            preferences.edit().putBoolean(LOCATION_SERVICE_STATUS, value).apply()
        }

    lateinit var currentShift : Shift

}