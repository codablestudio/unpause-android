package studio.codable.unpause.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.crashlytics.FirebaseCrashlytics
import studio.codable.unpause.BuildConfig
import studio.codable.unpause.app.di.ApplicationComponent
import studio.codable.unpause.app.di.ApplicationModule
import studio.codable.unpause.app.di.DaggerApplicationComponent
import studio.codable.unpause.utilities.logging.release.ReleaseTree
import timber.log.Timber

class App : Application() {
    companion object {
        lateinit var instance: App
            private set
    }

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        instance = this
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        initDaggerComponent()
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

    private fun initDaggerComponent() {
        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(instance))
            .build()
    }
}