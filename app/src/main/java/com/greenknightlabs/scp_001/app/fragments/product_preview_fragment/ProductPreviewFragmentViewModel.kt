package com.greenknightlabs.scp_001.app.fragments.product_preview_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.shopkeep.Shopkeep
import com.greenknightlabs.scp_001.app.util.shopkeep.objects.ProductProperties
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class ProductPreviewFragmentViewModel @Inject constructor(
    private val shopkeep: Shopkeep,
    private val navMan: NavMan
) : BaseViewModel() {
    // interfaces
    interface Listener {
        fun provideActivity(): MainActivity?
    }

    // properties
    var fragmentListener: ProductPreviewFragment.Listener? = null
    var vmListener: WeakReference<Listener>? = null
    var productProperties: ProductProperties? = null

    val isLocked = MutableLiveData(false)

    // functions
    fun handleOnTapBuy() {
        val activity = vmListener?.get()?.provideActivity() ?: return
        val productId = productProperties?.id ?: return
        isLocked.value = true
        state.value = PageState.Fetching

        viewModelScope.launch {
            try {
                shopkeep.purchase(activity, productId)
                isLocked.value = false
                state.value = PageState.Idle
                navMan.popFragment()
                fragmentListener?.onCompleteSuccessHandler()
            } catch (e: Throwable) {
                isLocked.value = false
                state.value = PageState.Idle
                toastMessage.value = e.message
            }
        }
    }
}
