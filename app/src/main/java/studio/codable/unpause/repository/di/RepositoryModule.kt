package studio.codable.unpause.repository.di

import dagger.Binds
import dagger.Module
import studio.codable.unpause.repository.*
import javax.inject.Named

@Module
abstract class RepositoryModule {

    @Binds
    @Named("firebaseUserRepository")
    internal abstract fun bindFirebaseUserRepository(firebaseUserRepository: FirebaseUserRepository): IUserRepository

    @Binds
    @Named("firebaseLoginRepository")
    internal abstract fun bindFirebaseLoginRepository(firebaseLoginRepository: FirebaseLoginRepository): ILoginRepository

    @Binds
    @Named("firebaseShiftRepository")
    internal abstract fun bindFirebaseShiftRepository(firebaseShiftRepository: FirebaseShiftRepository): IShiftRepository
}