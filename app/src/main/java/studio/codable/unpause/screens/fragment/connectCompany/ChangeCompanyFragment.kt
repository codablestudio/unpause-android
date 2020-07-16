package studio.codable.unpause.screens.fragment.connectCompany

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
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
        text_connect_company.text = getString(R.string.change_company)
        connect_company_button.text = getString(R.string.change_company)
    }

    private fun initListeners() {
        connect_company_button.setOnClickListener {
            showSoftwareKeyboard(false)
            userVm.updateCompany(edit_text_passcode.text.toString())
            showMessage(getString(R.string.company_updated))
            svm.navigateUp()
        }
    }
}