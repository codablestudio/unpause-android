package studio.codable.unpause.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import studio.codable.unpause.base.BaseViewModel
import studio.codable.unpause.model.User
import studio.codable.unpause.repository.FirebaseUserRepository
import studio.codable.unpause.repository.IUserRepository

class LoginViewModel : BaseViewModel() {

    private val firebaseRepository: IUserRepository =
        FirebaseUserRepository(FirebaseFirestore.getInstance())

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun createUser(email: String, firstName: String, lastName: String) {
        val user = User(email, firstName, lastName)
        viewModelScope.launch {
            process(firebaseRepository.createUser(user)) { newUser ->
                _user.value = newUser
            }
        }
    }

    fun getUser(id: String) {
        viewModelScope.launch {
            process(firebaseRepository.getUser(id)) {
                _user.value = it
            }
        }
    }
}