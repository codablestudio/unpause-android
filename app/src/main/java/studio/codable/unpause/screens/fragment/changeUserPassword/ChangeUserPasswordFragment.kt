package studio.codable.unpause.screens.fragment.changeUserPassword


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import studio.codable.unpause.R
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.screens.UserViewModel

class ChangeUserPasswordFragment : BaseFragment(false) {

    private val userVm: UserViewModel by activityViewModels()
    private var listener: OnChangeUserPasswordListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_user_password, container, false)
    }

    fun onUserPasswordChanged() {
        listener?.onUserPasswordChangeEvent()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnChangeUserPasswordListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnChangeUserPasswordListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnChangeUserPasswordListener {
        fun onUserPasswordChangeEvent()

    }

    private fun initUI() {

//        activity?.let {
//
//            mViewModel = ViewModelProviders.of(it).get(ChangeUserPasswordViewModel::class.java)
//
//            mViewModel?.errors?.observe(it, Observer { message ->
//                hideLoading()
//                showError(message.toString())
//            })
//
//            update_password_button.setOnClickListener {
//
//                showLoading()
//                mViewModel?.signInUser(
//                    user!!.email!!,
//                    old_password_edit_text.text.toString(),
//                    this@ChangeUserPasswordFragment
//                )?.observe(this@ChangeUserPasswordFragment, Observer {
//
//                    if (new_password_edit_text.text.toString().isNotBlank()) {
//
//                        mViewModel?.updateUserPassword(new_password_edit_text.text.toString())
//                            ?.observe(this@ChangeUserPasswordFragment, Observer {
//                                hideLoading()
//                                showMessage(getString(R.string.updated_password))
//                                onUserPasswordChanged()
//                            })
//                    } else {
//                        hideLoading()
//                        showError(getString(R.string.password_cannot_be_blank))
//                    }
//                })
//            }
//        }
    }
}
