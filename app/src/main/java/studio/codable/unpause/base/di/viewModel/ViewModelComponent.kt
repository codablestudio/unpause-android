package studio.codable.unpause.base.di.viewModel

import dagger.Subcomponent
import studio.codable.unpause.activity.LoginViewModel
import studio.codable.unpause.activity.SharedViewModel
import studio.codable.unpause.app.di.scope.PerViewModel

@PerViewModel
@Subcomponent
interface ViewModelComponent {

    fun inject(loginViewModel: LoginViewModel)
    fun inject(sharedViewModel: SharedViewModel)
}