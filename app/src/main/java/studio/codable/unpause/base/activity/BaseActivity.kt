package studio.codable.unpause.base.activity

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import studio.codable.unpause.app.App
import studio.codable.unpause.screens.activity.emailVerification.EmailVerificationActivity
import studio.codable.unpause.screens.activity.home.HomeActivity
import studio.codable.unpause.screens.activity.login.LoginActivity
import studio.codable.unpause.screens.activity.register.RegisterActivity
import studio.codable.unpause.screens.activity.start.StartActivity

abstract class BaseActivity : AppCompatActivity() {

    private val component = App.instance.applicationComponent

    init {
        inject()
    }

    private fun inject() {
        component.let {
            when (this) {
                is StartActivity -> it.plusActivity().inject(this)
                is LoginActivity -> it.plusActivity().inject(this)
                is RegisterActivity -> it.plusActivity().inject(this)
                is HomeActivity -> it.plusActivity().inject(this)
                is EmailVerificationActivity -> it.plusActivity().inject(this)
            }
        }
    }

    protected open fun showMessage(message: String?) {
        message?.let {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    protected open fun showError(message: String?) {
        message?.let {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}