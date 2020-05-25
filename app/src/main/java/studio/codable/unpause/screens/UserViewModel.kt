package studio.codable.unpause.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import studio.codable.unpause.base.viewModel.BaseViewModel
import studio.codable.unpause.model.User
import studio.codable.unpause.repository.IUserRepository
import studio.codable.unpause.utilities.manager.SessionManager
import javax.inject.Inject
import javax.inject.Named

class UserViewModel @Inject constructor(
    @Named("firebaseUserRepository")
    private val userRepository: IUserRepository,
    private val sessionManager: SessionManager
) : BaseViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    var isCheckedIn: Boolean
        get() = sessionManager.isCheckedIn
        set(value) {
            sessionManager.isCheckedIn = value
        }

    fun getUser() {
        viewModelScope.launch {
            process(userRepository.getUser(sessionManager.userId)) {
                _user.value = it
            }
        }
    }

    fun checkIn() {

    }

    fun checkOut() {

    }
}