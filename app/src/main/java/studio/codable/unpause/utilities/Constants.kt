package studio.codable.unpause.utilities

object Constants {

    object RequestCode {

        const val GOOGLE_SIGN_IN = 0
        const val FINE_LOCATION_PERMISSION = 11
        const val BACKGROUND_LOCATION_PERMISSION = 12
        const val CHECK_IN = 22
        const val CHECK_OUT = 21
    }




    object Geofencing {
        const val DEFAULT_RADIUS = 100.0F
    }
    object Notifications {
        const val CHECK_IN_CHECK_OUT_ID = 7
        const val KEY_DESCRIPTION = "key_description"
        const val NOTIFICATION_CHANNEL_ID = "unpause_notifications_channel"
    }

    object Actions {
        const val ACTION_CHECK_IN = "action_check_in"
        const val ACTION_CHECK_OUT = "action_check_out"
    }

    object FirestoreCollections {
        const val COMPANIES = "companies"
        const val USERS = "users"
    }
}