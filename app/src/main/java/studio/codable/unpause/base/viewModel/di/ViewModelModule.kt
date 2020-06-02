package studio.codable.unpause.base.viewModel.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import studio.codable.unpause.screens.SharedViewModel
import studio.codable.unpause.screens.UserViewModel
import studio.codable.unpause.screens.activity.emailVerification.EmailVerificationViewModel
import studio.codable.unpause.screens.activity.login.LoginViewModel
import studio.codable.unpause.screens.activity.register.RegisterViewModel
import studio.codable.unpause.screens.activity.start.StartViewModel

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SharedViewModel::class)
    internal abstract fun sharedViewModel(viewModel: SharedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun loginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StartViewModel::class)
    internal abstract fun startViewModel(viewModel: StartViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel::class)
    internal abstract fun registerViewModel(viewModel: RegisterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel::class)
    internal abstract fun userViewModel(viewModel: UserViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EmailVerificationViewModel::class)
    internal abstract fun emailVerificationViewModel(viewModel: EmailVerificationViewModel): ViewModel
}