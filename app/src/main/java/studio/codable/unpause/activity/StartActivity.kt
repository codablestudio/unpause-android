package studio.codable.unpause.activity

import android.os.Bundle
import studio.codable.unpause.base.BaseActivity

class StartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(LoginActivity.getIntent(this))
    }
}
