package studio.codable.unpause.utilities.manager

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class SessionManager @Inject constructor(context: Context) {

    companion object {
        private const val USER_ID = "user_id"
        private const val CHECK_IN_STATE = "check_in_state"
        private const val GEOFENCE_REQUEST_IDS = "geofence_request_ids"
        private const val delimiter = ","
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences("Session", Context.MODE_PRIVATE)

    var userId: String
        get() = preferences.getString(USER_ID, "").orEmpty()
        set(value) = preferences.edit().putString(USER_ID, value).apply()

    var isCheckedIn: Boolean
        get() = preferences.getBoolean(CHECK_IN_STATE, false)
        set(value) = preferences.edit().putBoolean(CHECK_IN_STATE, value).apply()

    var geofenceRequestIds: List<Int>
        get() {
            return preferences.getString(GEOFENCE_REQUEST_IDS,"")?.split(delimiter)?.map {
                    Integer.parseInt(it)
                } ?: arrayListOf()
        }
        set(value) {
            val builder = StringBuilder()
            value.forEach { builder.append(it).append(delimiter) }
            preferences.edit().putString(GEOFENCE_REQUEST_IDS, builder.toString()).apply()
        }

}