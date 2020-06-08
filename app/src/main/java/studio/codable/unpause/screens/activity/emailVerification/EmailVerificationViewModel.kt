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
import studio.codable.unpause.utilities.Event
import javax.inject.Inject
import javax.inject.Named

class EmailVerificationViewModel @Inject constructor(
    @Named("firebaseLoginRepository")
    private val loginRepository: ILoginRepository
) : BaseViewModel() {

    private val _userVerified : MediatorLiveData<Event<Boolean>> by lazy { MediatorLiveData<Event<Boolean>>() }
    val userVerified: LiveData<Event<Boolean>> = _userVerified

    private val verificationResult : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    private var verificationEmailSent : Boolean = false
    private val handler = Handler()
    private lateinit var email : String
    private lateinit var password : String
    private val runnable = Runnable {
        viewModelScope.launch {
            process(loginRepository.verifyEmail(email, password)) {
                if (!it && !verificationEmailSent) {
                    viewModelScope.launch {
                        process(loginRepository.sendVerificationEmail()) {
                        }
                        verificationEmailSent = true
                    }
                } else {
                    verificationResult.value = it
                }
            }
        }
    }

    init {
        _userVerified.value = Event(false)
        _userVerified.addSource(verificationResult, Observer {
            if (!it) {
                handler.postDelayed(runnable, 2000)
            } else {
                _userVerified.value = Event(true)
            }
        })
    }

    fun waitForEmailVerification(email: String, password: String) {
        this.email = email
        this.password = password
        handler.post(runnable)
    }
}