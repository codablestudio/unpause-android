package studio.codable.tbtl.base.di

import dagger.Subcomponent
import studio.codable.unpause.app.di.scope.PerActivity
import studio.codable.unpause.base.viewModel.di.ViewModelModule
import studio.codable.unpause.repository.di.RepositoryModule
import studio.codable.unpause.screens.activity.home.HomeActivity
import studio.codable.unpause.screens.activity.login.LoginActivity
import studio.codable.unpause.screens.activity.register.RegisterActivity
import studio.codable.unpause.screens.activity.start.StartActivity

@PerActivity
@Subcomponent(modules = [ViewModelModule::class, RepositoryModule::class])
interface ActivityComponent {

    fun inject(startActivity: StartActivity)
    fun inject(loginActivity: LoginActivity)
    fun inject(registerActivity: RegisterActivity)
    fun inject(homeActivity: HomeActivity)
}