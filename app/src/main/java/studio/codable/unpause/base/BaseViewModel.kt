package studio.codable.unpause.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studio.codable.unpause.utilities.Event
import studio.codable.unpause.utilities.networking.ErrorResponse
import studio.codable.unpause.utilities.networking.NetworkResult
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {

    private val _errors = MutableLiveData<Event<String>>()
    val errors: LiveData<Event<String>> = _errors

    protected val _loading = MediatorLiveData<Event<Boolean>>().apply {
        addSource(_errors) {
            this.value = Event(false)
        }
    }
    val loading: LiveData<Event<Boolean>> = _loading

    protected inline fun <T> process(result: NetworkResult<T>, onSuccess: (value: T) -> Unit) {
        when (result) {
            is NetworkResult.Success -> {
                _loading.value = Event(false) // _error LiveData sets this automatically
                Timber.tag(this::class.java.simpleName)
                    .i("Network call successful: ${result.value}")
                onSuccess(result.value)
            }
            is NetworkResult.GenericError -> {
                Timber.tag(this::class.java.simpleName).e("Network call failed: $result")
                showGenericError(result.code, result.error)
            }
            is NetworkResult.NetworkError -> {
                Timber.tag(this::class.java.simpleName).e("Network call failed: network error")
                showNetworkError()
            }
        }
    }

    protected open fun showGenericError(message: String?) {
        _errors.value = Event(message)
    }

    protected open fun showGenericError(code: Int?, error: ErrorResponse?) {
        error?.message?.let {
            _errors.value = Event(it)

        } ?: run {
            code?.let {
                _errors.value = Event(it.toString())
            }
        }
    }

    protected open fun showNetworkError() {
        _errors.value = Event("Network error")
    }
}