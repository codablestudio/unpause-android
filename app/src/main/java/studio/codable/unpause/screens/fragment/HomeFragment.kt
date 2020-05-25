package studio.codable.unpause.screens.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_home.*
import studio.codable.unpause.R
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.screens.UserViewModel
import timber.log.Timber

class HomeFragment : BaseFragment(true) {

    private val userVm: UserViewModel by activityViewModels()

    /**
     * isChecked -> user is checked in = state ON
     */
    private val checkInButtonListener: (CompoundButton, Boolean) -> Unit = { _, isChecked ->
        if (isChecked) {
            userVm.checkIn()
            Timber.d("checked in")
        } else {
            userVm.checkOut("test description")
            Timber.d("checked out")
        }
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
        btn_check_in_out.setOnCheckedChangeListener(checkInButtonListener)
    }

    private fun initObservers() {
        userVm.errors.observe(viewLifecycleOwner, Observer {
            showError(it.getContentIfNotHandled())
        })

        userVm.user.observe(viewLifecycleOwner, Observer {
            text_email.text = it.email
            text_first_name.text = it.firstName.orEmpty()
            text_last_name.text = it.lastName.orEmpty()
            text_company.text = "todo"
        })

        userVm.shifts.observe(viewLifecycleOwner, Observer {
            Timber.d("Shifts: $it")
        })
    }

}