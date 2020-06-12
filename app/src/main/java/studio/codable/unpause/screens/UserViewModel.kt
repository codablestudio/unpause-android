package studio.codable.unpause.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import studio.codable.unpause.base.viewModel.BaseViewModel
import studio.codable.unpause.model.Company
import studio.codable.unpause.model.Shift
import studio.codable.unpause.model.User
import studio.codable.unpause.repository.ICompanyRepository
import studio.codable.unpause.repository.IShiftRepository
import studio.codable.unpause.repository.IUserRepository
import studio.codable.unpause.utilities.extensions.active
import studio.codable.unpause.utilities.manager.SessionManager
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class UserViewModel @Inject constructor(
    @Named("firebaseUserRepository")
    private val userRepository: IUserRepository,
    @Named("firebaseShiftRepository")
    private val shiftRepository: IShiftRepository,
    @Named("firebaseCompanyRepository")
    private val companyRepository: ICompanyRepository,
    private val sessionManager: SessionManager
) : BaseViewModel() {

    private val _user = MediatorLiveData<User>()
    val user: LiveData<User> = _user

    private val _shifts = MutableLiveData<List<Shift>>()
    val shifts: LiveData<List<Shift>> = _shifts

    private val _company = MutableLiveData<Company>()
    val company: LiveData<Company> = _company

    init {
        getUser()
        _user.addSource(_shifts) {
            _user.value?.shifts = it as ArrayList<Shift>
            Timber.i("Shifts received: ${_shifts.value}")
        }
        _user.addSource(_company) {
            _user.value?.company = it
            Timber.i("Company received: ${_company.value}")
        }
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
                it.companyId?.let { id -> getCompany(id) }
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

    private fun getCompany(companyId : String) {
        viewModelScope.launch {
            process(companyRepository.getCompany(companyId)) {
                _company.value = it
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

    private fun updateUserInDatabase(user: User) {
        viewModelScope.launch {
                process(userRepository.updateUser(user)) {
                getUser()
                getShifts()
            }
        }
    }

    fun deleteShift(shift: Shift) {
        viewModelScope.launch {
            process(shiftRepository.delete(_user.value!!.id, shift)) {
                getShifts()
            }
        }
    }

    fun editShift(shift: Shift) {
        viewModelScope.launch {
            process(shiftRepository.update(_user.value!!.id, shift)) {
                getShifts()
            }
        }
    }

    fun addCustomShift(shift: Shift) {
        addShift(shift)
    }
}