package studio.codable.unpause.app.di

import dagger.Component
import studio.codable.tbtl.base.di.ActivityComponent
import studio.codable.unpause.app.di.scope.PerApplication
import studio.codable.unpause.base.fragment.di.FragmentComponent
import studio.codable.unpause.base.viewModel.di.ViewModelComponent
import studio.codable.unpause.utilities.networking.di.NetworkingModule

@PerApplication
@Component(modules = [ApplicationModule::class, NetworkingModule::class])
interface ApplicationComponent {
    fun plusActivity(): ActivityComponent
    fun plusViewModel(): ViewModelComponent
    fun plusFragment(): FragmentComponent
}