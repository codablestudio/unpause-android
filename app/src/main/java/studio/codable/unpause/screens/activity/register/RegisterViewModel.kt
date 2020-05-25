package studio.codable.unpause.screens.activity.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import studio.codable.unpause.base.viewModel.BaseViewModel
import studio.codable.unpause.repository.ILoginRepository
import studio.codable.unpause.utilities.Event
import studio.codable.unpause.utilities.manager.SessionManager
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

class RegisterViewModel @Inject constructor(
    @Named("firebaseLoginRepository")
    private val loginRepository: ILoginRepository,
    private val sessionManager: SessionManager
) : BaseViewModel(), CoroutineScope {

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    private val errorHandler = CoroutineExceptionHandler { _, throwable -> Timber.w(throwable) }
    private val vmJob = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + vmJob + errorHandler

    fun register(email: String, password: String, firstName: String?, lastName: String?) {
        _loading.value = Event(true)
        viewModelScope.launch {
            process(loginRepository.register(email, password, firstName, lastName)) {
                saveUserId(it)
                _userId.value = it
            }
        }
    }

    private fun saveUserId(id: String) {
        sessionManager.userId = id
    }
}