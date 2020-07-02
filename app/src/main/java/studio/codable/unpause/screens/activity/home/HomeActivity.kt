package studio.codable.unpause.screens.activity.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import kotlinx.android.synthetic.main.activity_home.*
import studio.codable.unpause.R
import studio.codable.unpause.base.activity.BaseActivity
import studio.codable.unpause.screens.UserViewModel
import studio.codable.unpause.utilities.Constants.Notifications.LOCATION_NOTIFICATION_CHANNEL_ID
import studio.codable.unpause.utilities.extensions.setupWithNavController
import studio.codable.unpause.utilities.manager.NotificationManagerUnpause
import javax.inject.Inject

class HomeActivity : BaseActivity() {

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    private val userVm: UserViewModel by viewModels { vmFactory }

    companion object {
        private const val USER_ID = "userId"

        fun getIntent(context: Context, userId: String): Intent {
            return Intent(context, HomeActivity::class.java).apply {
                putExtra(USER_ID, userId)
            }
        }
    }

    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupBottomNavigationBar()
        userVm
        NotificationManagerUnpause.createNotificationChannel(this, LOCATION_NOTIFICATION_CHANNEL_ID)

    }

    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(
            R.navigation.navigation_home,
            R.navigation.navigation_user_activity
//            R.navigation.navigation_settings
        )

        val controller = bottom_nav_main.setupWithNavController(
            navGraphIds, supportFragmentManager, R.id.nav_host_home_activity, intent
        )
        controller.observe(this, Observer { navController ->
//            setupActionBarWithNavController(navController) //todo I am not using this currently or I'm doing something wrong

        })

        currentNavController = controller
    }
}