package studio.codable.unpause.utilities

object Constants {

    object RequestCode {

        const val GOOGLE_SIGN_IN = 0
        const val FINE_LOCATION_PERMISSION = 11
    }

    const val NOTIFICATION_CHANNEL_ID = "schedule notifications channel"

    object Geofencing{
        const val DEFAULT_RADIUS = 100.0F
    }

    object FirestoreCollections {
        const val COMPANIES = "companies"
        const val USERS = "users"
    }
}