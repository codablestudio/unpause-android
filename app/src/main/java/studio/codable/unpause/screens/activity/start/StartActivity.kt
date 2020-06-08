package studio.codable.unpause.screens.activity.start

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import studio.codable.unpause.base.activity.BaseActivity
import studio.codable.unpause.screens.activity.home.HomeActivity
import studio.codable.unpause.screens.activity.login.LoginActivity
import javax.inject.Inject

class StartActivity : BaseActivity() {

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    private val vm: StartViewModel by viewModels { vmFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkForExistingUser()
    }

    private fun checkForExistingUser() {
        //uncomment to enable verification
        //if (vm.userId.isNotBlank() || (vm.userId.isNotBlank() && vm.isUserVerified()) )
        if (vm.userId.isNotBlank()) {
            startActivity(HomeActivity.getIntent(this, vm.userId))
            finish()
        } else {
            startActivity(
                LoginActivity.getIntent(this)
            )
            finish()
        }
    }
}
