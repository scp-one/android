package com.greenknightlabs.scp_001.app.fragments.appearance_fragment

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.Preferences
import com.greenknightlabs.scp_001.app.util.preferences.enums.PrefKey
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.app.fragments.product_preview_fragment.ProductPreviewFragment
import com.greenknightlabs.scp_001.app.util.shopkeep.Shopkeep
import com.greenknightlabs.scp_001.app.util.shopkeep.config.ShopkeepConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppearanceFragmentViewModel @Inject constructor(
    val preferences: Preferences,
    private val navMan: NavMan,
    private val shopkeep: Shopkeep
) : BaseViewModel(), ProductPreviewFragment.Listener {
    // properties
    private val onCompleteSuccessAction = MutableLiveData<() -> Unit>()

    // functions
    fun handleOnTapPreferenceComponent(prefKey: PrefKey, view: View) {
        val displayNames = prefKey.displayNames()
        val rawValues = prefKey.rawValues()

        view.makePopupMenu(displayNames) { index ->
            if (displayNames[index].endsWith(ShopkeepConstants.PREMIUM_INDICATOR)) {
                val customer = shopkeep.customer.value
                if (customer == null) {
                    toastMessage.value = "User does not appear to be logged in."
                    return@makePopupMenu
                }

                val productProperties = prefKey.productProperties(rawValues[index])
                if (productProperties == null) {
                    toastMessage.value = "Could not retrieve product properties."
                    return@makePopupMenu
                }

                if (customer.allPurchasedSkus.contains(productProperties.id)) {
                    preferences.set(prefKey, rawValues[index])
                    return@makePopupMenu
                }

                val unlockedByEntitlement = productProperties.unlockedByEntitlement
                if (unlockedByEntitlement != null) {
                    if (customer.entitlements[unlockedByEntitlement.rawValue] != null) {
                        preferences.set(prefKey, rawValues[index])
                        return@makePopupMenu
                    }
                }

                onCompleteSuccessAction.value = {
                    preferences.set(prefKey, rawValues[index])
                }

                val productPreviewFragment = ProductPreviewFragment()
                productPreviewFragment.productProperties = productProperties
                productPreviewFragment.listener = this
                navMan.pushFragment(productPreviewFragment, true)
            } else {
                preferences.set(prefKey, rawValues[index])
            }
        }
    }

    override fun onCompleteSuccessHandler() {
        onCompleteSuccessAction.value?.invoke()
        onCompleteSuccessAction.value = {}
    }
}
