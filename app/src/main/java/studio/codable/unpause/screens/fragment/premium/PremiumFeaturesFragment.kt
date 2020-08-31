package studio.codable.unpause.screens.fragment.premium

import androidx.fragment.app.activityViewModels
import studio.codable.unpause.base.fragment.BaseFragment
import studio.codable.unpause.screens.UserViewModel
import studio.codable.unpause.utilities.manager.SubscriptionManager

open class PremiumFeaturesFragment : BaseFragment(false) {

    protected val userVm: UserViewModel by activityViewModels()
    protected val subscriptionManager by lazy { SubscriptionManager.getInstance(requireContext()) }

    fun userIsPremium() : Boolean {
        val userIsPromo = userVm.user.value?.isPromoUser ?: false
        return userIsPromo || subscriptionManager.isUserSubscribed()
    }
}