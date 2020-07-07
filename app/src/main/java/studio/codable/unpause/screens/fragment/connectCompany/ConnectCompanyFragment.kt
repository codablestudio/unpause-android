package studio.codable.unpause.screens.fragment.connectCompany

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_connect_company.*
import studio.codable.unpause.R
import studio.codable.unpause.screens.activity.home.HomeActivity
import studio.codable.unpause.screens.activity.register.RegisterViewModel

class ConnectCompanyFragment : BaseCompanyFragment() {

    private val registerVm : RegisterViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initListeners()
    }

    private fun initUI() {
        text_connect_company.text = getString(R.string.connect_company)
        connect_company_button.text = getString(R.string.connect_company)
        skip_button.visibility = View.VISIBLE
        textView9.visibility = View.VISIBLE
    }

    private fun initListeners() {
        connect_company_button.setOnClickListener {
            if (edit_text_passcode.text.toString().isNotBlank()) {
                showLoading()
                registerVm.connectCompany(edit_text_passcode.text.toString())
                registerVm.finishRegistration()
            }
        }

        skip_button.setOnClickListener {
            registerVm.finishRegistration()
        }

        registerVm.registrationFinished.observe(viewLifecycleOwner, Observer {
            hideLoading()
        })

        registerVm.errors.observe(viewLifecycleOwner, Observer {
            hideLoading()
        })
    }

}