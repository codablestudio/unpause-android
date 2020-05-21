package studio.codable.unpause.base

import androidx.appcompat.app.AppCompatActivity
import studio.codable.unpause.activity.LoginActivity
import studio.codable.unpause.activity.StartActivity
import studio.codable.unpause.app.App

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
}