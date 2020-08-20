package studio.codable.unpause.utilities.manager

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.Constants.Subscriptions.SUBSCRIPTION_1
import studio.codable.unpause.utilities.Constants.Subscriptions.SUBSCRIPTION_2
import timber.log.Timber


/**
 * SubscriptionManager is class which serves for handling subscriptions and granting user
 * access to premium features.
 *
 * After initialization, method [connect] must be called to handle connecting to Google Play Billing
 * API. The method [disconnect] is used to close the connection.
 */
class SubscriptionManager(context: Context) : SkuDetailsResponseListener {

    override fun onSkuDetailsResponse(result: BillingResult, list: MutableList<SkuDetails>?) {
        Timber.i(list.toString())
    }

    private lateinit var subscriptions: List<Purchase>

    private val purchaseUpdateListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    processPurchase(purchase)
                }
            } else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                // Handle any other error codes.
            }

        }

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchaseUpdateListener)
        .enablePendingPurchases()
        .build()

    fun connect() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    // TODO: The BillingClient is ready. You can query purchases here.
                    subscriptions = billingClient.queryPurchases(BillingClient.SkuType.SUBS).purchasesList!!.toMutableList()
                }
            }

            override fun onBillingServiceDisconnected() {
                //TODO:
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    fun disconnect() {
        billingClient.endConnection()
    }

    fun querySkuDetails() {
        val skuList = ArrayList<String>()
        skuList.add(SUBSCRIPTION_1)
        skuList.add(SUBSCRIPTION_2)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        val skuDetailsResult = GlobalScope.launch {
            billingClient.querySkuDetailsAsync(params.build(), this@SubscriptionManager)
        }
    }

    private fun processPurchase(purchase: Purchase?) {
        purchase?.let {
            if (it.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!it.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
//                    val ackPurchaseResult = withContext(Dispatchers.IO) {
//                        billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
//                    }
                }

            }
        }
    }

    fun launchSubscriptionFlow(activity: Activity, params: BillingFlowParams) {
        billingClient.launchBillingFlow(activity, params)
    }

    fun isUserSubscribed() : Boolean {
        return subscriptions.isNotEmpty()
    }


}