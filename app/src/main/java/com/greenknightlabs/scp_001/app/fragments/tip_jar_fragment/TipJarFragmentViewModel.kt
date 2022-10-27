package com.greenknightlabs.scp_001.app.fragments.tip_jar_fragment

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.app.util.shopkeep.Shopkeep
import com.greenknightlabs.scp_001.app.util.shopkeep.enums.TipJarOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class TipJarFragmentViewModel @Inject constructor(
    private val shopkeep: Shopkeep
) : BaseViewModel() {
    // interfaces
    interface Listener {
        fun provideActivity(): MainActivity?
    }

    // properties
    var listener: WeakReference<Listener>? = null
    val isLocked = MutableLiveData(false)

    // functions
    fun handleOnTapBuy(tipJarOption: TipJarOptions) {
        val activity = listener?.get()?.provideActivity() ?: return
        isLocked.value = true
        state.value = PageState.Fetching

        viewModelScope.launch {
            try {
                shopkeep.purchase(activity, tipJarOption.productProperties().id)
                isLocked.value = false
                state.value = PageState.Idle
                toastMessage.value = "Thank you for your kindness!"
            } catch (e: Throwable) {
                isLocked.value = false
                state.value = PageState.Idle
                toastMessage.value = e.message
            }
        }
    }
}
