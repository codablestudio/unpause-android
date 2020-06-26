package studio.codable.unpause.utilities

object Constants {

    object RequestCode {

        const val GOOGLE_SIGN_IN = 0
        const val FINE_LOCATION_PERMISSION = 11
        const val BACKGROUND_LOCATION_PERMISSION = 12
    }

    const val NOTIFICATION_CHANNEL_ID = "schedule notifications channel"

    object Geofencing {
        const val DEFAULT_RADIUS = 100.0F
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