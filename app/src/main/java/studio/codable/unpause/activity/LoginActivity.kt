package studio.codable.unpause.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import studio.codable.unpause.R
import studio.codable.unpause.base.BaseActivity
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
        loginVm.login("filip@codable.studio", "abcd1244")
//        svm.getUser("filip@codable.studio")
//        svm.createUser("neki_mail", "nekoime", "neko prezime")

        loginVm.errors.observe(this, Observer {
            Toast.makeText(this, it.getContentIfNotHandled(), Toast.LENGTH_LONG).show()
        })

        loginVm.user.observe(this, Observer {
            Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
        })
    }
}
