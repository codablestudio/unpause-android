package studio.codable.unpause.screens.activity.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_login.*
import studio.codable.unpause.R
import studio.codable.unpause.base.activity.BaseActivity
import studio.codable.unpause.screens.activity.home.HomeActivity
import javax.inject.Inject

class LoginActivity : BaseActivity() {

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    private val loginVm: LoginViewModel by viewModels { vmFactory }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initUI()
        initObservers()
    }

    private fun initObservers() {
        loginVm.errors.observe(this, Observer {
            showMessage(it.getContentIfNotHandled())
        })

        loginVm.userId.observe(this, Observer {
            loginVm.saveUserId(it)
            startActivity(HomeActivity.getIntent(this, it))
        })
    }

    private fun initUI() {
        btn_login.setOnClickListener {
            if (isInputValid()) {
                loginVm.login(text_email.text.toString(), text_password.text.toString())
            } else {
                showMessage(getString(R.string.invalid_input))
            }
        }
    }

    private fun isInputValid(): Boolean {
        return text_email.text.isNotBlank() && text_password.text.isNotBlank()
    }
}
