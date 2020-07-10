package studio.codable.unpause.utilities

object Constants {

    object RequestCode {

        const val GOOGLE_SIGN_IN = 0
        const val FINE_LOCATION_PERMISSION = 11
        const val BACKGROUND_LOCATION_PERMISSION = 12
        const val CHECK_IN = 22
        const val CHECK_OUT = 21
        const val CHECK_IN_CHECK_OUT_SERVICE = 123
    }

    object Geofencing {
        const val DEFAULT_RADIUS = 100.0F
    }

    object Notifications {
        const val CHECK_IN_CHECK_OUT_ID = 7
        const val CHECK_IN_CHECK_OUT_SERVICE_ID = 2121
        const val KEY_DESCRIPTION = "key_description"
        const val LOCATION_NOTIFICATION_CHANNEL_ID = "unpause_notifications_channel"
        const val CHECK_IN_TAG = "check_in"
        const val CHECK_OUT_TAG = "check_out"
    }

    object Actions {
        const val ACTION_CHECK_IN = "action_check_in"
        const val ACTION_CHECK_OUT = "action_check_out"
        const val GEOFENCING_ACTION = "geofence_broadcast"
    }

    object Chart {
        val xAxisLabels = arrayListOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
    }

    object FirestoreCollections {
        const val COMPANIES = "companies"
        const val USERS = "users"
    }
}