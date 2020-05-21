package studio.codable.unpause.screens.activity.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import studio.codable.unpause.R
import studio.codable.unpause.base.activity.BaseActivity

class HomeActivity : BaseActivity() {

    companion object {
        private const val USER_ID = "userId"

        fun getIntent(context: Context, userId: String): Intent {
            return Intent(context, HomeActivity::class.java).apply {
                putExtra(USER_ID, userId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        showMessage(intent.getStringExtra(USER_ID))
    }
}