package studio.codable.unpause.base.activity

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import studio.codable.unpause.app.App
import studio.codable.unpause.screens.activity.login.LoginActivity
import studio.codable.unpause.screens.activity.start.StartActivity

abstract class BaseActivity : AppCompatActivity() {

    private val component = App.instance.applicationComponent

    init {
        inject()
    }

    private fun inject() {
        component.let {
            when (this) {
                is StartActivity -> component.plusActivity().inject(this)
                is LoginActivity -> component.plusActivity().inject(this)
            }
        }
    }

    protected open fun showMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}