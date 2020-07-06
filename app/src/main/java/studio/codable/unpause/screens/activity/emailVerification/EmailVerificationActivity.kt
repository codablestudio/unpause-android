package studio.codable.unpause.screens.activity.emailVerification

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.activity_email_verification.*
import studio.codable.unpause.R
import studio.codable.unpause.base.activity.BaseActivity
import studio.codable.unpause.screens.SharedViewModel
import studio.codable.unpause.screens.activity.home.HomeActivity
import studio.codable.unpause.utilities.animation.AnimListener
import javax.inject.Inject

class EmailVerificationActivity : BaseActivity() {

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    private val verificationVm: EmailVerificationViewModel by viewModels { vmFactory }
    override val navController: NavController by lazy { findNavController(R.id.navigation_email_verification_activity) }
    private val svm: SharedViewModel by viewModels { vmFactory }

    companion object {
        private const val EMAIL = "email"
        private const val PASSWORD = "password"

        fun getIntent(context: Context, email: String, password: String): Intent {
            return Intent(context, EmailVerificationActivity::class.java).apply {
                putExtra(EMAIL, email)
                putExtra(PASSWORD, password)
            }
        }
    }

    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verification)

        email = intent.getStringExtra(EMAIL)
        password = intent.getStringExtra(PASSWORD)

        svm // instantiated to become available to all BaseFragment instances

        initObservers()
        initUI()
    }
    private fun initUI() {
        anim_email_sent.playAnimation()
        text_email.text = email

        verificationVm.waitForEmailVerification(email, password)
    }
    private fun initObservers() {
        verificationVm.userVerified.observe(this, Observer {
            if (it.hasBeenHandled) {
                emailConfirmedAnimation.addAnimatorListener((object: AnimListener() {
                    override fun onAnimationEnd(animation: Animator?) {
                        startActivity(HomeActivity.getIntent(this@EmailVerificationActivity, email))
                        finish()
                    }
                }))
                emailConfirmedAnimation.playAnimation()
            }
        })
    }
}
