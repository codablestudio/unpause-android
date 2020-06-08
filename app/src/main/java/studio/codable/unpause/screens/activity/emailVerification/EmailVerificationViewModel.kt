package studio.codable.unpause.screens.activity.emailVerification

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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

    private val _userVerified : MediatorLiveData<Boolean> by lazy { MediatorLiveData<Boolean>() }
    val userVerified : LiveData<Boolean>
        get() {
            return _userVerified
        }

    private val result : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    private var verificationEmailSent : Boolean = false
    private val handler = Handler()
    private lateinit var _email : String
    private lateinit var _password : String
    private val runnable = Runnable {
        viewModelScope.launch {
            process(loginRepository.verifyEmail(_email, _password)) {
                if (!it && verificationEmailSent.not()) {
                    viewModelScope.launch {
                        process(loginRepository.sendVerificationEmail()) {
                            verificationEmailSent = true
                        }
                    }
                }
                result.value = it
            }
        }
    }

    init {

        _userVerified.value = false
        _userVerified.addSource(result, Observer {
            if (!it) {
                handler.postDelayed(runnable, 2000)
            } else {
                _userVerified.value = true
            }
        })
    }

    fun waitForEmailVerification(email: String, password: String) {
        _email = email
        _password = password
        handler.post(runnable)
    }
}