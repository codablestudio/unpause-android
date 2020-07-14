package studio.codable.unpause.screens.fragment.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_register.*
import studio.codable.unpause.R
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.screens.activity.home.HomeActivity
import studio.codable.unpause.screens.activity.register.RegisterViewModel

class RegisterFragment : BaseFragment(false) {

    private val registerVm: RegisterViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() {
        btn_register.setOnClickListener {
            if (checkFields()) {
                showLoading()
                registerVm.register(
                    text_email.text.toString(),
                    text_password.text.toString(),
                    text_first_name.text.toString(),
                    text_last_name.text.toString()
                )
            } else {
                showMessage("Invalid input")
            }
        }

        registerVm.registrationComplete.observe(viewLifecycleOwner, Observer {
                hideLoading()
                svm.navigate(RegisterFragmentDirections.actionRegisterFragmentToConnectCompanyFragment())
        })

        registerVm.loading.observe(viewLifecycleOwner, Observer {
            defaultHandleLoading(it)
        })
    }
    private fun checkFields(): Boolean {
        return text_email.text.isNotBlank() && text_password.text.isNotBlank()
    }
}