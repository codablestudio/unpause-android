package studio.codable.tbtl.util.logging.release

import com.google.firebase.crashlytics.FirebaseCrashlytics

class CrashlyticsReportingLibrary private constructor() {

    init {
        throw AssertionError("No instances.")
    }

    companion object {
        fun logWarning(t: Throwable?) {
            t?.let {
                with(FirebaseCrashlytics.getInstance()) { this.recordException(it) }
            }
        }

        fun logError(t: Throwable?, message: String?) {
            with(FirebaseCrashlytics.getInstance()) {
                this.log(message.orEmpty())
                t?.let {
                    this.recordException(it)
                }
            }
        }

        fun logWtf(t: Throwable?, message: String?) {
            with(FirebaseCrashlytics.getInstance()) {
                this.log(message.orEmpty())
                t?.let { this.recordException(it) }
            }
        }
    }
}