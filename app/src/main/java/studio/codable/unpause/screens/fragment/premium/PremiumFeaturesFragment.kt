package studio.codable.unpause.screens.fragment.premium

import androidx.fragment.app.activityViewModels
import studio.codable.unpause.base.activity.BaseActivity
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.screens.UserViewModel
import studio.codable.unpause.utilities.manager.DialogManager
import studio.codable.unpause.utilities.manager.SubscriptionManager

open class PremiumFeaturesFragment : BaseFragment(false) {

    protected val userVm: UserViewModel by activityViewModels()
    protected val subscriptionManager by lazy { SubscriptionManager.getInstance(requireContext()) }
    protected val dialogManager: DialogManager by lazy { DialogManager(activity as BaseActivity) }

    protected var userIsPremium: Boolean = false
        get() = (userVm.user.value?.isPromoUser ?: false) || subscriptionManager.isUserSubscribed()
        private set

    protected fun launchPremiumScreen() {
        dialogManager.openUpgradeToPremiumDialog({
            subscriptionManager.launchSubscriptionFlowMonthly(requireActivity())
        }, {
            subscriptionManager.launchSubscriptionFlowYearly(requireActivity())
        })
    }
}