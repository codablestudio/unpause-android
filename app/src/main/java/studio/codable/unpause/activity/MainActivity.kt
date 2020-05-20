package studio.codable.unpause.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import studio.codable.unpause.R
import studio.codable.unpause.base.BaseActivity

class MainActivity : BaseActivity() {

    private val svm: SharedViewModel by viewModels()

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        svm
    }
}
