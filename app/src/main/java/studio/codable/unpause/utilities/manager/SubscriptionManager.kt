package studio.codable.unpause.utilities.manager

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode

class SubscriptionManager(context: Context) {

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


    init {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    // TODO: The BillingClient is ready. You can query purchases here.
                }
            }

            override fun onBillingServiceDisconnected() {
                //TODO:
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })

    }

//        fun querySkuDetails() {
//        val skuList = ArrayList<String>()
//        skuList.add("premium_upgrade")
//        skuList.add("gas")
//        val params = SkuDetailsParams.newBuilder()
//        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
//        val skuDetailsResult = withContext(Dispatchers.IO) {
//            billingClient.querySkuDetailsAsync(params.build())
//        }
//        // Process the result.
//    }
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


}