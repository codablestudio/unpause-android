package studio.codable.unpause.screens.activity.start

import studio.codable.unpause.base.viewModel.BaseViewModel
import studio.codable.unpause.repository.ILoginRepository
import studio.codable.unpause.utilities.manager.SessionManager
import javax.inject.Inject
import javax.inject.Named

class StartViewModel @Inject constructor(@Named("firebaseLoginRepository")
                                         private val loginRepository: ILoginRepository,
                                         private val sessionManager: SessionManager) :
    BaseViewModel() {

    var userId: String
        get() = sessionManager.userId
        set(value) {
            sessionManager.userId = value
        }

    fun isUserVerified() : Boolean {
        return loginRepository.isUserVerified()
    }
}