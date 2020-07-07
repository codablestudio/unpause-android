package studio.codable.unpause.screens.activity.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.activity_register.*
import studio.codable.unpause.R
import studio.codable.unpause.base.activity.BaseActivity
import studio.codable.unpause.screens.SharedViewModel
import studio.codable.unpause.screens.activity.home.HomeActivity
import javax.inject.Inject

class RegisterActivity : BaseActivity() {

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    private val registerVm: RegisterViewModel by viewModels { vmFactory }
    override val navController: NavController by lazy { findNavController(R.id.navigation_register_activity) }
    private val svm: SharedViewModel by viewModels { vmFactory }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, RegisterActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        svm // instantiated to become available to all BaseFragment instances
        registerVm

        initObservers()
    }

    private fun initObservers() {
        registerVm.errors.observe(this, Observer {
            showError(it.getContentIfNotHandled())
        })

        registerVm.registrationFinished.observe(this, Observer {
            if (it) {
                startActivity(HomeActivity.getIntent(this, registerVm.getUserID()))
                finish()
            }
        })
    }
}
