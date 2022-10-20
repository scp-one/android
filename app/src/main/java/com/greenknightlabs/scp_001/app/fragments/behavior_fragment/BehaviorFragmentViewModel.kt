package com.greenknightlabs.scp_001.app.fragments.behavior_fragment

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.Preferences
import com.greenknightlabs.scp_001.app.util.preferences.enums.PrefKey
import com.greenknightlabs.scp_001.app.util.shopkeep.Shopkeep
import com.greenknightlabs.scp_001.app.util.shopkeep.config.ShopkeepConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BehaviorFragmentViewModel @Inject constructor(
    val preferences: Preferences,
    private val navMan: NavMan,
    private val shopkeep: Shopkeep
) : BaseViewModel() {
    // functions
    fun handleOnTapPreferenceComponent(prefKey: PrefKey, view: View) {
        val displayNames = prefKey.displayNames()
        val rawValues = prefKey.rawValues()

        view.makePopupMenu(displayNames) { index ->
            if (displayNames[index].endsWith(ShopkeepConstants.PREMIUM_INDICATOR)) {
                val customer = shopkeep.customer.value
                if (customer == null) {
                    toastMessage.value = "User  does not appear to be logged in."
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

                toastMessage.value = "Show preview now"
            } else {
                preferences.set(prefKey, rawValues[index])
            }
        }
    }
}
