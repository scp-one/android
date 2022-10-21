package com.greenknightlabs.scp_001.app.util.shopkeep

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.greenknightlabs.scp_001.app.config.AppConstants
import com.greenknightlabs.scp_001.users.enums.UserAccessLevel
import com.greenknightlabs.scp_001.users.enums.UserEntitlements
import com.revenuecat.purchases.*
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class Shopkeep @Inject constructor(
    private val context: Context
) : UpdatedCustomerInfoListener {
    // properties
    private val _customer = MutableLiveData<CustomerInfo?>(null)
    val customer: LiveData<CustomerInfo?> get() = _customer

    // init
    fun configure(uid: String?) {
        var configuration = PurchasesConfiguration.Builder(context, AppConstants.REVENUE_CAT_API_KEY)

        if (uid != null) {
            configuration = configuration.appUserID(uid)
        }

        Purchases.debugLogsEnabled = true
        Purchases.configure(configuration.build())
        Purchases.sharedInstance.updatedCustomerInfoListener = this
    }

    // functions
    fun login(uid: String) {
        Purchases.sharedInstance.logInWith(
            uid,
            { error ->
                Timber.e(error.message)
            },
            { customerInfo, created ->
                _customer.value = customerInfo
            }
        )
    }

    fun logout() {
        Purchases.sharedInstance.logOutWith(
            { error ->
                Timber.e(error.message)
            },
            { _ ->
                // success
            }
        )
        _customer.value = null
    }

    @Throws
    suspend fun purchase(activity: Activity, productId: String): Nothing? = suspendCoroutine { cont ->
        Purchases.sharedInstance.getNonSubscriptionSkusWith(
            listOf(productId),
            { error ->
                Timber.e(error.message)
                cont.resumeWithException(Throwable(error.message))
            },
            { storeProducts ->
                if (storeProducts.isEmpty()) {
                    cont.resumeWithException(Throwable("No products found."))
                }

                Purchases.sharedInstance.purchaseProductWith(
                    activity,
                    storeProducts.first(),
                    { error, userCancelled ->
                        Timber.e(error.message)
                        val error = if (userCancelled) Throwable("Canceled") else Throwable(error.message)
                        cont.resumeWithException(error)
                    },
                    { _, customerInfo ->
                        _customer.value = customerInfo
                        cont.resume(null)
                    }
                )
            }
        )
    }

    suspend fun restore(): Nothing? = suspendCoroutine { cont ->
        Purchases.sharedInstance.restorePurchasesWith(
            { error ->
                cont.resumeWithException(Throwable(error.message))
            },
            { customerInfo ->
                _customer.value = customerInfo
                cont.resume(null)
            }
        )
    }

    fun hasSupportAccess(): Boolean {
        return hasEntitlement(UserEntitlements.Supporter)
    }

    fun hasProAccess(): Boolean {
        return hasEntitlement(UserEntitlements.Pro)
    }

    private fun hasEntitlement(entitlement: UserEntitlements): Boolean {
        return customer.value?.entitlements?.get(entitlement.rawValue) != null
    }

    // UpdatedCustomerInfoListener
    override fun onReceived(customerInfo: CustomerInfo) {
        _customer.value = customerInfo
    }
}
