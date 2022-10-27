package com.greenknightlabs.scp_001.app.fragments.pro_access_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.app.util.shopkeep.Shopkeep
import com.greenknightlabs.scp_001.app.util.shopkeep.standalone_products.ProAccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class ProAccessFragmentViewModel @Inject constructor(
    private val shopkeep: Shopkeep
) : BaseViewModel() {
    // interfaces
    interface Listener {
        fun provideActivity(): MainActivity?
    }

    // properties
    val product = ProAccess()
    var fragmentListener: ProAccessFragment.Listener? = null
    var vmListener: WeakReference<Listener>? = null
    val isLocked = MutableLiveData(false)
    val hasUnlockedProAccess = MutableLiveData(shopkeep.hasProAccess())

    // functions
    fun handleOnTapBuy() {
        val activity = vmListener?.get()?.provideActivity() ?: return
        isLocked.value = true
        state.value = PageState.Fetching

        viewModelScope.launch {
            try {
                shopkeep.purchase(activity, product.id)
                hasUnlockedProAccess.value = shopkeep.hasProAccess()
                isLocked.value = false
                state.value = PageState.Idle
            } catch (e: Throwable) {
                isLocked.value = false
                state.value = PageState.Idle
                toastMessage.value = e.message
            }
        }
    }

    fun handleOnTapRestore() {
        isLocked.value = true
        state.value = PageState.Fetching

        viewModelScope.launch {
            try {
                shopkeep.restore()
                hasUnlockedProAccess.value = shopkeep.hasProAccess()
                isLocked.value = false
                state.value = PageState.Idle
            } catch (e: Throwable) {
                isLocked.value = false
                state.value = PageState.Idle
                toastMessage.value = e.message
            }
        }
    }
}
