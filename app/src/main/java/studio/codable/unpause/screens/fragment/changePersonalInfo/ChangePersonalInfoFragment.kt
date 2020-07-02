package studio.codable.unpause.screens.fragment.changePersonalInfo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import studio.codable.unpause.R
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.model.User
import studio.codable.unpause.screens.UserViewModel

class ChangePersonalInfoFragment : BaseFragment(false) {

    private val userVm: UserViewModel by activityViewModels()
    private var listener: OnChangePersonalInfoListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_personal_info, container, false)
    }

    fun onPersonalInfoChangeEvent(user: User) {
        listener?.onPersonalInfoChangeEvent(user)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnChangePersonalInfoListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnChangePersonalInfoListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnChangePersonalInfoListener {
        fun onPersonalInfoChangeEvent(user: User)

    }

    private fun initUI() {

//        activity?.let {
//
//            mViewModel = ViewModelProviders.of(it).get(ChangePersonalInfoViewModel::class.java)
//
//            mViewModel?.errors?.observe(this@ChangePersonalInfoFragment, Observer { message ->
//                hideLoading()
//                showError(message.toString())
//            })
//
//            update_personal_info_button.setOnClickListener {
//
//                var changed = false
//
//                if (first_name_edit_text.text.toString().isNotBlank()) {
//                    mViewModel?.updateFirstName(user, first_name_edit_text.text.toString())
//                    changed = true
//                }
//
//                if (last_name_edit_text.text.toString().isNotBlank()) {
//                    mViewModel?.updateLastName(user, last_name_edit_text.text.toString())
//                    changed = true
//                }
//
//                if (changed) {
//                    showLoading()
//                    mViewModel?.updateUser(user!!)?.observe(this@ChangePersonalInfoFragment, Observer {
//                        hideLoading()
//                        onPersonalInfoChangeEvent(user!!)
//                        showMessage(getString(R.string.updated_user_info))
//                        closeFragment()
//                    })
//                } else {
//                    showError(getString(R.string.fields_cannot_be_blank))
//                }
//            }
//        }
    }
}
