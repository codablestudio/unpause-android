package studio.codable.unpause.screens.activity.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
import studio.codable.unpause.base.viewModel.BaseViewModel
import studio.codable.unpause.model.User
import studio.codable.unpause.repository.ILoginRepository
import studio.codable.unpause.repository.IUserRepository
import studio.codable.unpause.utilities.Event
import studio.codable.unpause.utilities.manager.SessionManager
import studio.codable.unpause.utilities.networking.Result
import studio.codable.unpause.utilities.networking.TaskResult
import studio.codable.unpause.utilities.networking.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class LoginViewModel @Inject constructor(
    @Named("firebaseLoginRepository")
    private val loginRepository: ILoginRepository,
    @Named("firebaseUserRepository")
    private val userRepository: IUserRepository,
    private val sessionManager: SessionManager
) : BaseViewModel() {

    private val _userId: MediatorLiveData<String> by lazy { MediatorLiveData<String>() }
    val userId: LiveData<String> by lazy { _userId }

    private val _passwordSent : MutableLiveData<Event<Boolean>> by lazy { MutableLiveData<Event<Boolean>>() }
    val passwordSent : LiveData<Event<Boolean>> = _passwordSent

    private val _userVerified : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val userVerified : LiveData<Boolean> = _userVerified

    init {
        _userId.addSource(userId) {
            saveUserId(it)
        }
    }

    fun login(email: String, password: String) {
        _loading.value = Event(true)
        viewModelScope.launch {
            process(loginRepository.login(email, password)) {
//                  uncomment to enable verification
//                if (loginRepository.isUserVerified()) {
                    _userId.value = it
//                } else {
//                    _userVerified.value = false
//                }
            }
        }
    }

    fun logInWithCredentials(account: Task<GoogleSignInAccount>, clientId: String) {
        _loading.value = Event(true)
        viewModelScope.launch {
            processTask(account.await()) {
                process(loginRepository.signInWithGoogle(it, clientId)) {
                    sessionManager.userId = account.result!!.email!!
                    when (userRepository.getUser())  {

                        is Result.GenericError -> process( userRepository.createUser(
                            User(account.result!!.email!!, account.result!!.email!!,
                                account.result!!.displayName!!.split(" ")[0],
                                account.result!!.displayName!!.split(" ")[1])
                            )) {
                                _userId.value = account.result!!.email!!
                            }

                        is Result.Success -> _userId.value = account.result!!.email!!
                    }
                }
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            process(loginRepository.sendPasswordResetEmail(email)) {
                _passwordSent.value = Event(true)
            }
        }
    }

    private inline fun <T> processTask(
        result: TaskResult<T>,
        success: (data: T) -> Unit
    ) {
        _loading.value = Event(false)
        when (result) {
            is TaskResult.Success -> {
                Timber.i("Task successful: ${result.data}")
                success(result.data)
            }
            is TaskResult.Error -> {
                Timber.e(result.exception, "Task failed")
                showGenericError(result.exception.message)
            }
            is TaskResult.Canceled -> {
                Timber.w(result.exception, "Task canceled")
                showGenericError(result.exception?.message)
            }
        }
    }

    private fun saveUserId(id: String) {
        sessionManager.userId = id
    }
}