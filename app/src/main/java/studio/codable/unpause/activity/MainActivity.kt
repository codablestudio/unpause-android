package studio.codable.unpause.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import studio.codable.unpause.R
import studio.codable.unpause.base.BaseActivity

class MainActivity : BaseActivity() {

    private val svm: LoginViewModel by viewModels()

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        svm.getUser("filip@codable.studio")
//        svm.createUser("neki_mail", "nekoime", "neko prezime")

        svm.errors.observe(this, Observer {
            Toast.makeText(this, it.getContentIfNotHandled(), Toast.LENGTH_LONG).show()
        })
    }
}
