package studio.codable.unpause.screens.activity.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import studio.codable.unpause.base.viewModel.BaseViewModel
import studio.codable.unpause.repository.ICompanyRepository
import studio.codable.unpause.repository.ILoginRepository
import studio.codable.unpause.repository.IUserRepository
import studio.codable.unpause.utilities.Event
import studio.codable.unpause.utilities.manager.SessionManager
import javax.inject.Inject
import javax.inject.Named

class RegisterViewModel @Inject constructor(
    @Named("firebaseUserRepository")
    private val userRepository: IUserRepository,
    @Named("firebaseCompanyRepository")
    private val companyRepository: ICompanyRepository,
    @Named("firebaseLoginRepository")
    private val loginRepository: ILoginRepository,
    private val sessionManager: SessionManager
) : BaseViewModel(), CoroutineScope {

    //used to navigate to HomeActivity
    private val _registrationFinished = MutableLiveData<Unit>()
    val registrationFinished: LiveData<Unit> = _registrationFinished

    //used to navigate to ConnectCompanyFragment
    private val _registrationComplete = MutableLiveData<Unit>()
    val registrationComplete: LiveData<Unit> = _registrationComplete

    fun register(email: String, password: String, firstName: String?, lastName: String?) {
        _loading.value = Event(true)
        viewModelScope.launch {
            process(loginRepository.register(email, password, firstName, lastName)) {
                saveUserId(it)
                process(loginRepository.login(email, password)) {
                    _registrationComplete.value = null
                }
            }
        }
    }

    fun connectCompany(passcode : String) {
        viewModelScope.launch {
            process(companyRepository.getCompanyId(passcode)) {
                process(userRepository.updateCompany(sessionManager.userId, it)) {
                    finishRegistration()
                }
            }
        }
    }

    fun finishRegistration() {
        _registrationFinished.value = null
    }

    fun getUserID() : String = sessionManager.userId

    private fun saveUserId(id: String) {
        sessionManager.userId = id
    }
}