package studio.codable.unpause.screens.fragment.changePersonalInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_change_personal_info.*
import studio.codable.unpause.R
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.screens.UserViewModel

class ChangePersonalInfoFragment : BaseFragment(false) {

    private val userVm: UserViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initListeners()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_personal_info, container, false)
    }

    private fun initUI() {
        userVm.user.value?.let {
            first_name_edit_text.setText(it.firstName)
            last_name_edit_text.setText(it.lastName)
        }
    }

    private fun initListeners() {
        update_personal_info_button.setOnClickListener {
            if (areFieldsValid()) {
                userVm.updateFirstName(first_name_edit_text.text.toString())
                userVm.updateLastName(last_name_edit_text.text.toString())
                showMessage(getString(R.string.updated_user_info))
                svm.navigateUp()
            } else {
                showError(getString(R.string.fields_cannot_be_blank))
            }
        }
    }

    private fun areFieldsValid(): Boolean =
        first_name_edit_text.text.toString().isNotBlank() && last_name_edit_text.text.toString()
            .isNotBlank()
}
