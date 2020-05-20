package studio.codable.unpause.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
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