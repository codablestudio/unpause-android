package studio.codable.unpause.screens.fragment.changeUserPassword


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_change_user_password.*
import studio.codable.unpause.R
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.screens.UserViewModel

class ChangeUserPasswordFragment : BaseFragment(false) {

    private val userVm: UserViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_user_password, container, false)
    }

    private fun initListeners() {

        update_password_button.setOnClickListener {


            if (new_password_edit_text.text.toString().isNotBlank()) {

                userVm.updatePassword(
                    old_password_edit_text.text.toString(),
                    new_password_edit_text.text.toString()
                )
                showMessage(getString(R.string.updated_password))
                svm.navigateUp()

            } else {
                showError(getString(R.string.password_cannot_be_blank))
            }
        }
    }
}
