package studio.codable.unpause.screens.fragment.upgradeToPremium

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import studio.codable.unpause.R
import studio.codable.unpause.base.fragment.BaseFragment

class UpgradeToPremiumFragment : BaseFragment(false) {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upgrade_to_premium, container, false)
    }

    //TODO: change to fullscreen fragment dialog, add logic to the buttons
}