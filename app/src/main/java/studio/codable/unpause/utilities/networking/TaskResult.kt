package studio.codable.unpause.utilities.networking

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.concurrent.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

sealed class TaskResult<out R> {
    data class Success<out T>(val data: T) : TaskResult<T>()
    data class Error(val exception: Exception) : TaskResult<Nothing>()
    data class Canceled(val exception: Exception?) : TaskResult<Nothing>()
}

suspend fun <T> Task<T>.await(): TaskResult<T> {
    if (isComplete) {
        val e = exception
        Timber.e(e)
        return if (e == null) {
            if (isCanceled) {
                TaskResult.Canceled(CancellationException("Task $this was cancelled normally."))
            } else {
                @Suppress("UNCHECKED_CAST")
                TaskResult.Success(result as T)
            }
        } else {
            TaskResult.Error(e)
        }
    }

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            val e = exception
            Timber.e(e)
            if (e == null) {
                @Suppress("UNCHECKED_CAST")
                if (isCanceled) cont.cancel() else cont.resume(
                    TaskResult.Success(
                        result as T
                    )
                )
            } else {
                cont.resumeWithException(e)
            }
        }
    }
}