package studio.codable.tbtl.base.di

import dagger.Subcomponent
import studio.codable.unpause.activity.LoginActivity
import studio.codable.unpause.activity.StartActivity
import studio.codable.unpause.app.di.scope.PerActivity
import studio.codable.unpause.base.di.viewModel.ViewModelModule
import studio.codable.unpause.repository.di.RepositoryModule

@PerActivity
@Subcomponent(modules = [ViewModelModule::class, RepositoryModule::class])
interface ActivityComponent {

    fun inject(startActivity: StartActivity)
    fun inject(loginActivity: LoginActivity)
}