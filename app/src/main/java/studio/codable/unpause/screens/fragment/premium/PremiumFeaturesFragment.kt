package studio.codable.unpause.screens.fragment.premium

import androidx.fragment.app.activityViewModels
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.screens.UserViewModel
import studio.codable.unpause.utilities.manager.SubscriptionManager

open class PremiumFeaturesFragment : BaseFragment(false) {

    protected val userVm: UserViewModel by activityViewModels()
    protected val subscriptionManager by lazy { SubscriptionManager.getInstance(requireContext()) }

    protected var userIsPremium: Boolean = false
        get() = (userVm.user.value?.isPromoUser ?: false) || subscriptionManager.isUserSubscribed()
        private set
}