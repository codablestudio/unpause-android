package studio.codable.unpause.utilities.manager

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class SessionManager @Inject constructor(context: Context) {

    companion object {
        private const val USER_ID = "user_id"
        private const val CHECK_IN_STATE = "check_in_state"
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences("Session", Context.MODE_PRIVATE)

    var userId: String
        get() = preferences.getString(USER_ID, "").orEmpty()
        set(value) = preferences.edit().putString(USER_ID, value).apply()

    var isCheckedIn: Boolean
        get() = preferences.getBoolean(CHECK_IN_STATE, false)
        set(value) = preferences.edit().putBoolean(CHECK_IN_STATE, value).apply()
}