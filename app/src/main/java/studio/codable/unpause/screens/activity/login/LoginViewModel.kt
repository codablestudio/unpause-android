package studio.codable.unpause.screens.activity.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import studio.codable.unpause.base.viewModel.BaseViewModel
import studio.codable.unpause.repository.ILoginRepository
import studio.codable.unpause.utilities.manager.SessionManager
import javax.inject.Inject
import javax.inject.Named

class LoginViewModel @Inject constructor(
    @Named("firebaseLoginRepository")
    private val loginRepository: ILoginRepository,
    private val sessionManager: SessionManager
) : BaseViewModel() {

    private val _userId: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val userId: LiveData<String> by lazy { _userId }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            process(loginRepository.login(email, password)) {
                _userId.value = it
            }
        }
    }

    fun saveUserId(id: String) {
        sessionManager.userId = id
    }
}