package studio.codable.unpause.utilities.networking.di

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import studio.codable.unpause.app.di.scope.PerApplication

@Module
class NetworkingModule {

    @Provides
    @PerApplication
    fun provideFirebaseAuth(firebaseApp: FirebaseApp) = Firebase.auth(firebaseApp)

    @Provides
    @PerApplication
    fun provideFirebaseFirestore(firebaseApp: FirebaseApp) = Firebase.firestore(firebaseApp)
}