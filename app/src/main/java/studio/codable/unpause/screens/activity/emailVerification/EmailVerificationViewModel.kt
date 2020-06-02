package studio.codable.unpause.screens.activity.emailVerification

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import studio.codable.unpause.base.viewModel.BaseViewModel
import studio.codable.unpause.repository.ILoginRepository
import javax.inject.Inject
import javax.inject.Named

class EmailVerificationViewModel @Inject constructor(
    @Named("firebaseLoginRepository")
    private val loginRepository: ILoginRepository
) : BaseViewModel() {

    private val _userVerified : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val userVerified : LiveData<Boolean>
        get() {
            return _userVerified
        }

    init {
        _userVerified.value = false
    }

    fun waitForEmailVerification(email: String, password: String) {
        //TODO: send verification email to the user
        val handler = Handler()

        val runnable = object : Runnable {
            override fun run() {
                var result : Boolean = false
                viewModelScope.launch {
                    process(loginRepository.verifyEmail(email, password)) {
                       result = it
                    }
                }
                if (!result) {
                        handler.postDelayed(this, 2000)
                }
                else {
                    _userVerified.value = true
                }
            }
        }
        handler.post(runnable)
    }
}