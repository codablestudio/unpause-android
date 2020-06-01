package studio.codable.unpause.utilities.networking

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

sealed class Result<out T> {
    data class Success<out T>(val value: T) : Result<T>()
    data class GenericError(val errorResponse: ErrorResponse? = null) :
        Result<Nothing>()

    object IOError : Result<Nothing>()
}

data class ErrorResponse(val code: Int? = null, val exception: Exception? = null)

suspend fun <T> callApi(apiCall: suspend () -> T): Result<T> {
    return withContext(Dispatchers.IO) {
        try {
            val result = Result.Success(apiCall())
            Timber.i("Successful HTTP request: $result")
            result
        } catch (e: Exception) { // TODO
            when (e) {
                is IOException -> {
                    Timber.wtf(e, "Cannot access server")
                    Result.IOError
                }
                else -> {
                    Timber.e(e, "Unknown network error")
                    Result.GenericError(ErrorResponse(exception = e))
                }
            }
        }
    }
}

suspend fun <T, R> callFirebase(firestoreQuery: Task<T>, onSuccess: suspend (T) -> R): Result<R> {
    return withContext(Dispatchers.IO) {
        try {
            when (val res = firestoreQuery.await()) {
                is TaskResult.Success -> Result.Success(onSuccess(res.data))
                is TaskResult.Error -> Result.GenericError(ErrorResponse(exception = res.exception))
                is TaskResult.Canceled -> Result.GenericError(ErrorResponse(exception = res.exception))
            }
        } catch (e: Exception) { // for when nested call fails
            Result.GenericError(ErrorResponse(exception = e))
        }
    }
}

suspend fun <T, R> callFirebaseRawResult(firestoreQuery: Task<T>, onSuccess: suspend (T) -> R): R {
    return withContext(Dispatchers.IO) {
        when (val res = firestoreQuery.await()) {
            is TaskResult.Success -> onSuccess(res.data)
            is TaskResult.Error -> throw res.exception
            is TaskResult.Canceled -> res.exception?.let { throw it }
                ?: throw CancellationException("Irregular cancellation happened (probably)")
        }
    }
}