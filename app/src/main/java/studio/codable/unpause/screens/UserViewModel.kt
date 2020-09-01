package studio.codable.unpause.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import studio.codable.unpause.base.viewModel.BaseViewModel
import studio.codable.unpause.model.Company
import studio.codable.unpause.model.Location
import studio.codable.unpause.model.Shift
import studio.codable.unpause.model.User
import studio.codable.unpause.repository.*
import studio.codable.unpause.utilities.Event
import studio.codable.unpause.utilities.extensions.active
import studio.codable.unpause.utilities.geofencing.GeofenceModel
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
    @Named("firebaseLoginRepository")
    private val loginRepository: ILoginRepository,
    @Named("firebaseLocationRepository")
    private val locationRepository: ILocationRepository,
    private val sessionManager: SessionManager
) : BaseViewModel() {

    private val _user = MediatorLiveData<User>()
    val user: LiveData<User> = _user

    private val _shifts = MutableLiveData<List<Shift>>()
    val shifts: LiveData<List<Shift>> = _shifts

    private val _company = MutableLiveData<Company>()
    val company: LiveData<Company> = _company

    private val _geofences = MediatorLiveData<List<GeofenceModel>>()
    val geofences: LiveData<List<GeofenceModel>> = _geofences

    private val _locations = MutableLiveData<List<Location>>()
    val locations: LiveData<List<Location>> = _locations

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

        _geofences.addSource(_company) {
            getGeofences(_company.value!!.id)
            _geofences.removeSource(_locations)
        }
        _geofences.addSource(_locations) {
            getGeofencesFromLocations()
            _geofences.removeSource(_company)
        }

    }

    var isCheckedIn: Boolean
        get() = sessionManager.isCheckedIn
        private set(value) {
            sessionManager.isCheckedIn = value
        }

    fun checkIn() {
        val newShift = Shift(arrivalTime = Date())
        addShift(newShift)
        isCheckedIn = true
    }

    fun checkOut(description: String) {
        addExit(Date(), description)
        isCheckedIn = false
    }

    private fun getUser() {
        viewModelScope.launch {
            _loading.value = Event(true)
            process(userRepository.getUser()) {
                _user.value = it
                //if the user is connected to company get company, otherwise
                //get locations if there are any
                it.companyId?.let{id ->
                    getCompany(id)
                }?: getLocations()
            }
        }
    }

    private fun getShifts() {
        viewModelScope.launch {
            process(shiftRepository.getAll()) {
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

    private fun getGeofences(companyId : String) {
        viewModelScope.launch {
            process(companyRepository.getGeofences(company.value!!.id)) {
                _geofences.value = it
            }
        }
    }

    private fun getGeofencesFromLocations() {
        _geofences.value = _locations.value?.map {
            GeofenceModel(it)
        }
    }

    private fun addShift(shift: Shift) {
        viewModelScope.launch {
            process(shiftRepository.addNew(shift)) {
                getShifts()
            }
        }
    }

    private fun addExit(exitTime: Date, description: String) {
        viewModelScope.launch {
            shifts.value.active().let { active ->
                active?.let {
                    val updated = active.copy().addExit(exitTime, description)
                    process(shiftRepository.update(active, updated)) {
                        getShifts()
                    }
                }
            }
        }
    }

    fun deleteShift(shift: Shift) {
        viewModelScope.launch {
            process(shiftRepository.delete(shift)) {
                getShifts()
            }
        }
    }

    fun editShift(oldShift: Shift,newShift: Shift) {
        viewModelScope.launch {
            process(shiftRepository.update(oldShift, newShift)) {
                getShifts()
            }
        }
    }

    fun addCustomShift(shift: Shift) {
        addShift(shift)
    }

    fun signOut() {
        sessionManager.userId = ""
        loginRepository.signOut()
    }

    fun updateFirstName(firstName: String) {
        viewModelScope.launch {
            process(userRepository.updateFirstName(_user.value!!.id, firstName)) {
                getUser()
            }
        }
    }

    fun updateLastName(lastName: String) {
        viewModelScope.launch {
            process(userRepository.updateLastName(_user.value!!.id, lastName)) {
                getUser()
            }
        }
    }

    fun updatePassword(oldPassword : String, newPassword : String) {
        viewModelScope.launch {
            process(loginRepository.login(_user.value!!.email,oldPassword)) {
                process(loginRepository.updatePassword(newPassword)) {}
            }
        }
    }

    fun updateCompany(passcode: String) {
        viewModelScope.launch {
            process(companyRepository.getCompanyId(passcode)) {
                process(userRepository.updateCompany(_user.value!!.id, it)) {
                    getUser()
                }
            }
        }
    }

    fun getLocations() {
        viewModelScope.launch {
            process(locationRepository.getAll()) {
                _locations.value = it
            }
        }
    }

    fun addLocation(location: Location) {
        viewModelScope.launch {
            process(locationRepository.addLocation(location)) {
                Timber.i("Successfully added new location: '$location'")
                getLocations()
            }
        }
    }

    fun deleteLocation(location: Location) {
        viewModelScope.launch {
            process(locationRepository.deleteLocation(location)) {
                Timber.i("Successfully deleted location: '$location'")
                getLocations()
            }
        }
    }

    fun userHasConnectedCompany() : Boolean {
        return _company.value!=null
    }
}