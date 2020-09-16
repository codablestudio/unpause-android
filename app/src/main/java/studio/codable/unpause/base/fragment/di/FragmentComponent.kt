package studio.codable.unpause.base.fragment.di

import dagger.Subcomponent
import studio.codable.unpause.app.di.scope.PerActivity
import studio.codable.unpause.app.di.scope.PerFragment
import studio.codable.unpause.base.viewModel.di.ViewModelModule
import studio.codable.unpause.repository.di.RepositoryModule
import studio.codable.unpause.screens.fragment.HomeFragment
import studio.codable.unpause.screens.fragment.locations.LocationsListFragment
import studio.codable.unpause.screens.fragment.locations.MapFragment
import studio.codable.unpause.screens.fragment.settings.SettingsFragment

@PerActivity
@PerFragment
@Subcomponent(modules = [ViewModelModule::class, RepositoryModule::class])
interface FragmentComponent {
    fun inject(frag: HomeFragment)
    fun inject(frag: MapFragment)
    fun inject(frag: SettingsFragment)
    fun inject(frag: LocationsListFragment)
}