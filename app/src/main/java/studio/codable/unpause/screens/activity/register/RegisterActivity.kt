package studio.codable.unpause.screens.activity.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import studio.codable.unpause.R
import studio.codable.unpause.base.activity.BaseActivity

class RegisterActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, RegisterActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }
}
