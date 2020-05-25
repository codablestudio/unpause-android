package studio.codable.unpause.screens.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_home.*
import studio.codable.unpause.R
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.screens.UserViewModel

class HomeFragment : BaseFragment(true) {

    private val userVm: UserViewModel by activityViewModels()

    private val checkInButtonListener = View.OnClickListener {
        TODO("Not yet implemented")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initObservers()
    }

    private fun initUI() {
        btn_check_in_out.setOnClickListener(checkInButtonListener)
    }

    private fun initObservers() {
        userVm.user.observe(viewLifecycleOwner, Observer {
            text_email.text = it.email
            text_first_name.text = it.firstName.orEmpty()
            text_last_name.text = it.lastName.orEmpty()
            text_company.text = "todo"
        })
    }

}