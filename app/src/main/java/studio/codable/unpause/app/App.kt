package studio.codable.unpause.app

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import studio.codable.tbtl.util.logging.release.ReleaseTree
import studio.codable.unpause.BuildConfig
import timber.log.Timber

class App : Application() {
    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        initLogging()
    }

    private fun initLogging() {
        if (BuildConfig.BUILD_TYPE != "release") {
            Timber.plant(Timber.DebugTree())
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        } else {
            Timber.plant(ReleaseTree())
        }
    }
}