package studio.codable.unpause.screens.activity.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_login.*
import studio.codable.unpause.R
import studio.codable.unpause.base.activity.BaseActivity
import studio.codable.unpause.screens.activity.home.HomeActivity
import studio.codable.unpause.screens.activity.register.RegisterActivity
import studio.codable.unpause.utilities.Constants
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.RequestCode.GOOGLE_SING_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            loginVm.logInWithCredentials(task, getString(R.string.default_web_client_id))
        }
    }

    private fun initObservers() {
        loginVm.errors.observe(this, Observer {
            showError(it.getContentIfNotHandled())
        })

        loginVm.userId.observe(this, Observer {
            startActivity(HomeActivity.getIntent(this, it))
            finish()
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

        btn_register.setOnClickListener {
            startActivity(RegisterActivity.getIntent(this))
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        btn_sign_in_with_google.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, Constants.RequestCode.GOOGLE_SING_IN)
        }
    }

    private fun isInputValid(): Boolean {
        return text_email.text.isNotBlank() && text_password.text.isNotBlank()
    }
}
