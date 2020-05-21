package studio.codable.unpause.base.di

import dagger.Subcomponent
import studio.codable.unpause.app.di.scope.PerActivity
import studio.codable.unpause.app.di.scope.PerFragment
import studio.codable.unpause.base.di.viewModel.ViewModelModule
import studio.codable.unpause.repository.di.RepositoryModule

@PerActivity
@PerFragment
@Subcomponent(modules = [ViewModelModule::class, RepositoryModule::class])
interface FragmentComponent {
//    fun inject(frag: Frag)
}