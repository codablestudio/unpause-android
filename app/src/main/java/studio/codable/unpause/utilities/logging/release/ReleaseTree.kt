package studio.codable.tbtl.util.logging.release

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber


class ReleaseTree : Timber.Tree() {

    init {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return
        }

        if (priority == Log.ERROR) {
            CrashlyticsReportingLibrary.logError(t, message)
        } else if (priority == Log.WARN) {
            CrashlyticsReportingLibrary.logWarning(t)
        }
    }

    override fun wtf(t: Throwable?, message: String?, vararg args: Any?) {
        CrashlyticsReportingLibrary.logWtf(t, message)
    }
}
