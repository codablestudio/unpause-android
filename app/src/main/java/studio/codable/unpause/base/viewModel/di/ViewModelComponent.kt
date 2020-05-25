package studio.codable.unpause.base.viewModel.di

import dagger.Subcomponent
import studio.codable.unpause.app.di.scope.PerViewModel
import studio.codable.unpause.screens.SharedViewModel
import studio.codable.unpause.screens.activity.login.LoginViewModel
import studio.codable.unpause.screens.activity.register.RegisterViewModel
import studio.codable.unpause.screens.activity.start.StartViewModel

@PerViewModel
@Subcomponent
interface ViewModelComponent {

    fun inject(loginViewModel: LoginViewModel)
    fun inject(sharedViewModel: SharedViewModel)
    fun inject(startViewModel: StartViewModel)
    fun inject(registerViewModel: RegisterViewModel)
}