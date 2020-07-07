package studio.codable.unpause.screens.fragment.connectCompany

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import studio.codable.unpause.R
import studio.codable.unpause.base.fragment.BaseFragment
abstract class BaseCompanyFragment : BaseFragment(false) {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_connect_company, container, false)
    }
}