package studio.codable.unpause.utilities.networking

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

sealed class NetworkResult<out T> {
    data class Success<out T>(val value: T) : NetworkResult<T>()
    data class GenericError(val code: Int? = null, val error: ErrorResponse? = null) :
        NetworkResult<Nothing>()

    object NetworkError : NetworkResult<Nothing>()
}

data class ErrorResponse(val message: String)

suspend fun <T> callApi(apiCall: suspend () -> T): NetworkResult<T> {
    return withContext(Dispatchers.IO) {
        try {
            val result = NetworkResult.Success(apiCall.invoke())
            Timber.i("Successful HTTP request: $result")
            result
        } catch (throwable: Throwable) { // TODO
            when (throwable) {
                is IOException -> {
                    Timber.wtf(throwable, "Cannot access server")
                    NetworkResult.NetworkError
                }
                else -> {
                    Timber.e(throwable, "Unknown network error")
                    NetworkResult.GenericError(null, ErrorResponse(throwable.message.toString()))
                }
            }
        }
    }
}