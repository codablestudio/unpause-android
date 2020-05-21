package studio.codable.unpause.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import studio.codable.unpause.base.BaseViewModel
import studio.codable.unpause.model.User
import studio.codable.unpause.repository.ILoginRepository
import studio.codable.unpause.repository.IUserRepository
import javax.inject.Inject
import javax.inject.Named

class LoginViewModel @Inject constructor(
    @Named("firebaseUserRepository")
    private val userRepository: IUserRepository,
    @Named("firebaseLoginRepository")
    private val loginRepository: ILoginRepository
) : BaseViewModel() {

    private val _user: MutableLiveData<User> by lazy { MutableLiveData<User>() }
    val user: LiveData<User> by lazy { _user }

    fun createUser(email: String, firstName: String, lastName: String) {
        val user = User(email, firstName, lastName)
        viewModelScope.launch {
            process(userRepository.createUser(user)) { newUser ->
                _user.value = newUser
            }
        }
    }

    fun getUser(id: String) {
        viewModelScope.launch {
            process(userRepository.getUser(id)) {
                _user.value = it
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            process(loginRepository.login(email, password)) {
                _user.value = it
            }
        }
    }
}