package studio.codable.unpause.screens.activity.start

import studio.codable.unpause.base.viewModel.BaseViewModel
import studio.codable.unpause.utilities.manager.SessionManager
import javax.inject.Inject

class StartViewModel @Inject constructor(private val sessionManager: SessionManager) :
    BaseViewModel() {

    var userId: String
        get() = sessionManager.userId
        set(value) {
            sessionManager.userId = value
        }
}