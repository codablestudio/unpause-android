package studio.codable.unpause.utilities.manager

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
class SubscriptionManager private constructor(private val context: Context) : SkuDetailsResponseListener {

    companion object {
        @Volatile
        private var INSTANCE: SubscriptionManager? = null

        fun getInstance(context: Context): SubscriptionManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: SubscriptionManager(context)
                        .also { INSTANCE = it }
            }
    }

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


    /**
    * Receives the result from [querySkuDetails].
    *
    * Store the SkuDetails and post them in the [availableSubscriptions]. This allows other parts
    * of the app to use the [SkuDetails] to show SKU information and make purchases.
    */
    override fun onSkuDetailsResponse(
        billingResult: BillingResult,
        skuDetailsList: MutableList<SkuDetails>?) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        when (responseCode) {
            BillingResponseCode.OK -> {
                Timber.i("SkuDetails query responded with success. List: $skuDetailsList")
                availableSubscriptions = skuDetailsList ?: arrayListOf()

            }
            else -> {
                Timber.w("SkuDetails query failed. Response code: $responseCode, Message: $debugMessage")
            }
        }
    }

    private var subscriptions: List<Purchase> = arrayListOf()
    private lateinit var availableSubscriptions: List<SkuDetails>
    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchaseUpdateListener)
        .enablePendingPurchases() // Not used for subscriptions.
        .build()

    fun connect() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    Timber.i("Connected to Google Play Billing API.")
                    getActiveSubscriptions()
                    querySkuDetails()
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
        Timber.i("Disconnected from Google Play Billing API.")
    }

    private fun querySkuDetails() {
        val params = SkuDetailsParams.newBuilder()
            .setType(BillingClient.SkuType.SUBS)
            .setSkusList(listOf(
                SUBSCRIPTION_1,
                SUBSCRIPTION_2
            ))
            .build()
        params?.let { skuDetailsParams ->
            billingClient.querySkuDetailsAsync(skuDetailsParams, this)
        }
    }

    private fun processPurchase(purchase: Purchase?) {
        purchase?.let {
            if (it.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!it.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken).build()
                    CoroutineScope(Dispatchers.IO).launch {
                        billingClient.acknowledgePurchase(acknowledgePurchaseParams)
                    }
                }
                getActiveSubscriptions()
            }
        }
    }

    private fun getActiveSubscriptions() {
        subscriptions =
            billingClient.queryPurchases(BillingClient.SkuType.SUBS).purchasesList?.toMutableList()
                ?: arrayListOf()
        Timber.i("Currently active subscriptions: $subscriptions")
    }

    fun launchSubscriptionFlowMonthly(activity: Activity) {
        billingClient.launchBillingFlow(activity, BillingFlowParams.newBuilder()
            .setSkuDetails(availableSubscriptions[0])
            .build()
        )
    }

    fun launchSubscriptionFlowYearly(activity: Activity) {
        billingClient.launchBillingFlow(activity, BillingFlowParams.newBuilder()
            .setSkuDetails(availableSubscriptions[1])
            .build()
        )
    }

    fun isUserSubscribed() : Boolean {
        return subscriptions.isNotEmpty()
    }



}