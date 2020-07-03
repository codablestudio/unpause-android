package studio.codable.unpause.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDirections
import studio.codable.unpause.base.viewModel.BaseViewModel
import studio.codable.unpause.utilities.Event
import studio.codable.unpause.utilities.navigation.NavCommand
import javax.inject.Inject

class SharedViewModel @Inject constructor() : BaseViewModel() {
    private val _navCommand = MutableLiveData<Event<NavCommand>>()
    val navCommand: LiveData<Event<NavCommand>> = _navCommand

    fun navigate(directions: NavDirections) {
        _navCommand.postValue(Event(NavCommand.To(directions)))
    }

    fun navigateUp() {
        _navCommand.postValue(Event(NavCommand.Up))
    }
}