package studio.codable.unpause.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import studio.codable.unpause.base.viewModel.BaseViewModel
import studio.codable.unpause.model.Shift
import studio.codable.unpause.model.User
import studio.codable.unpause.repository.IShiftRepository
import studio.codable.unpause.repository.IUserRepository
import studio.codable.unpause.utilities.extensions.active
import studio.codable.unpause.utilities.manager.SessionManager
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class UserViewModel @Inject constructor(
    @Named("firebaseUserRepository")
    private val userRepository: IUserRepository,
    @Named("firebaseShiftRepository")
    private val shiftRepository: IShiftRepository,
    private val sessionManager: SessionManager
) : BaseViewModel() {

    private val _user = MediatorLiveData<User>()
    val user: LiveData<User> = _user

    private val _shifts = MutableLiveData<List<Shift>>()
    val shifts: LiveData<List<Shift>> = _shifts

    init {
        _user.addSource(_shifts, androidx.lifecycle.Observer {
            _user.value?.shifts = it as ArrayList<Shift>
        })
        getUser()
        getShifts()
    }

    var isCheckedIn: Boolean
        get() = sessionManager.isCheckedIn
        set(value) {
            sessionManager.isCheckedIn = value
        }

    fun checkIn() {
        val newShift = Shift(arrivalTime = Date())
        addShift(newShift)
    }

    fun checkOut(description: String) {
        addExit(Date(), description)
    }

    private fun getUser() {
        viewModelScope.launch {
            process(userRepository.getUser(sessionManager.userId)) {
                _user.value = it
            }
        }
    }

    private fun getShifts() {
        viewModelScope.launch {
            process(shiftRepository.getAll(sessionManager.userId)) {
                _shifts.value = it
            }
        }
    }

    private fun addShift(shift: Shift) {
        viewModelScope.launch {
            process(shiftRepository.addNew(sessionManager.userId, shift)) {
                getShifts()
            }
        }
    }

    private fun addExit(exitTime: Date, description: String) {
        viewModelScope.launch {
            shifts.value.active().let { active ->
                val updated = active.copy().addExit(exitTime, description)
                process(shiftRepository.update(sessionManager.userId, updated)) {
                    getShifts()
                }
            }
        }
    }

    fun updateUserInDatabase(user: User) {
        viewModelScope.launch {
            process(userRepository.updateUser(user)) {}
        }
    }

    fun deleteShift(shift: Shift) {
        viewModelScope.launch {
            process(shiftRepository.delete(_user.value!!.id, shift)) {
                updateUserInDatabase(_user.value!!)
            }
        }
    }

    fun editShift(shift: Shift) {
        viewModelScope.launch {
            process(shiftRepository.update(_user.value!!.id, shift)) {
                updateUserInDatabase(_user.value!!)
            }
        }
    }
}