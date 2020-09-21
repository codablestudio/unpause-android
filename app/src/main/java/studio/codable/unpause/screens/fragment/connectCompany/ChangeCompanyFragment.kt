package studio.codable.unpause.screens.fragment.connectCompany

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_change_personal_info.*
import kotlinx.android.synthetic.main.fragment_connect_company.*
import studio.codable.unpause.R
import studio.codable.unpause.screens.UserViewModel

class ChangeCompanyFragment : BaseCompanyFragment() {

    private val userVm: UserViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initListeners()
    }

    private fun initUI() {
        text_connect_company.text = getCompanyStringForView()
        connect_company_button.text = getCompanyStringForView()
    }

    private fun initListeners() {

        userVm.companyExists.observe(viewLifecycleOwner, Observer { companyExists ->
            companyExists.getContentIfNotHandled()?.let {
                if (it) {
                    userVm.updateCompany(edit_text_passcode.text.toString())
                    showMessage(getString(R.string.company_updated))
                    svm.navigateUp()
                } else {
                    showMessage("Company with provided passcode does not exist.")
                }
            }

        })

        connect_company_button.setOnClickListener {
            showSoftwareKeyboard(false)
            userVm.handleCompanyUpdate(edit_text_passcode.text.toString())

        }
    }

    private fun getCompanyStringForView(): String = if (userVm.userHasConnectedCompany()) {
        getString(R.string.change_company)
    } else {
        getString(R.string.connect_company)
    }

    override fun onResume() {
        super.onResume()
        edit_text_passcode.text.clear()
    }
}