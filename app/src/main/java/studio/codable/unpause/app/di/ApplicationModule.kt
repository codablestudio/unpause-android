package studio.codable.unpause.app.di

import android.content.Context
import com.google.firebase.FirebaseApp
import dagger.Module
import dagger.Provides
import studio.codable.unpause.app.App
import studio.codable.unpause.app.di.scope.PerApplication

@Module
class ApplicationModule(private val app: App) {

    @PerApplication
    @Provides
    fun provideApp(): App = app

    @PerApplication
    @Provides
    fun provideContext(): Context = app.applicationContext

    @PerApplication
    @Provides
    fun provideFirebaseApp(): FirebaseApp = FirebaseApp.initializeApp(app.applicationContext)!!
}