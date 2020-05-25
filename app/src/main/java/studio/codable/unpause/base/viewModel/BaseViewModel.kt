package studio.codable.unpause.base.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studio.codable.unpause.app.App
import studio.codable.unpause.screens.SharedViewModel
import studio.codable.unpause.screens.activity.login.LoginViewModel
import studio.codable.unpause.screens.activity.register.RegisterViewModel
import studio.codable.unpause.screens.activity.start.StartViewModel
import studio.codable.unpause.utilities.Event
import studio.codable.unpause.utilities.networking.ErrorResponse
import studio.codable.unpause.utilities.networking.Result
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {

    private val _errors: MutableLiveData<Event<String>> by lazy { MutableLiveData<Event<String>>() }
    val errors: LiveData<Event<String>> by lazy { _errors }

    protected val _loading: MediatorLiveData<Event<Boolean>> by lazy {
        MediatorLiveData<Event<Boolean>>().apply {
            addSource(_errors) {
                this.value = Event(false)
            }
        }
    }
    val loading: LiveData<Event<Boolean>> by lazy { _loading }

    private val component = App.instance.applicationComponent

    init {
        inject()
    }

    private fun inject() {
        component.let {
            when (this) {
                is LoginViewModel -> it.plusViewModel().inject(this)
                is SharedViewModel -> it.plusViewModel().inject(this)
                is StartViewModel -> it.plusViewModel().inject(this)
                is RegisterViewModel -> it.plusViewModel().inject(this)
            }
        }
    }

    protected inline fun <T> process(result: Result<T>, onSuccess: (value: T) -> Unit) {
        when (result) {
            is Result.Success -> {
                _loading.value = Event(false) // _error LiveData sets this automatically
                Timber.tag(this::class.java.simpleName)
                    .i("Network call successful: ${result.value}")
                onSuccess(result.value)
            }
            is Result.GenericError -> {
                Timber.tag(this::class.java.simpleName).e("Network call failed: $result")
                showGenericError(result.errorResponse)
            }
            is Result.IOError -> {
                Timber.tag(this::class.java.simpleName).e("Network call failed: network error")
                showNetworkError()
            }
        }
    }

    protected open fun showGenericError(message: String?) {
        _errors.value = Event(message)
    }

    protected open fun showGenericError(error: ErrorResponse?) {
        error?.exception?.let {
            _errors.value = Event(it.message)

        } ?: run {
            error?.code?.let {
                _errors.value = Event(it.toString())
            }
        }
    }

    protected open fun showNetworkError() {
        _errors.value = Event("Network error")
    }
}