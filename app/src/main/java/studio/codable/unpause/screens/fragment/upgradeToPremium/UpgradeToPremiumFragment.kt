package studio.codable.unpause.screens.fragment.upgradeToPremium

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_upgrade_to_premium.*
import studio.codable.unpause.R
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.utilities.LambdaNoArgumentsUnit

class UpgradeToPremiumFragment : DialogFragment() {

    private var dialogListenerOnBuySubscription1: LambdaNoArgumentsUnit? = null
    private var dialogListenerOnBuySubscription2: LambdaNoArgumentsUnit? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upgrade_to_premium, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    private fun initUI() {
        btn_subscription_1.setOnClickListener {
            dialogListenerOnBuySubscription1?.invoke()
            dismiss()
        }

        btn_subscription_2.setOnClickListener {
            dialogListenerOnBuySubscription2?.invoke()
            dismiss()
        }
    }

    fun setOnBuySubscription1Listener(onBuySubscriptionListener : LambdaNoArgumentsUnit) {
        dialogListenerOnBuySubscription1 = onBuySubscriptionListener
    }

    fun setOnBuySubscription2Listener(onBuySubscriptionListener : LambdaNoArgumentsUnit) {
        dialogListenerOnBuySubscription2 = onBuySubscriptionListener
    }

    //TODO: change to fullscreen fragment dialog, add logic to the buttons
}