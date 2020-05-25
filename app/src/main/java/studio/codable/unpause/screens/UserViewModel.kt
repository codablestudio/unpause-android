package studio.codable.unpause.screens

import androidx.lifecycle.LiveData
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

    init {
        getUser()
        getShifts()
    }

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _shifts = MutableLiveData<List<Shift>>()
    val shifts: LiveData<List<Shift>> = _shifts

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
}