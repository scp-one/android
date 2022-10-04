package com.greenknightlabs.scp_001.app.fragments.appearance_fragment

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.Preferences
import com.greenknightlabs.scp_001.app.util.preferences.enums.PrefKey
import com.greenknightlabs.scp_001.app.view_models.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AppearanceFragmentViewModel @Inject constructor(
    val preferences: Preferences,
    private val navMan: NavMan
) : BaseViewModel() {
    // properties
    val popupMenuView = MutableLiveData<View?>(null)
    val popupMenuPrefKey = MutableLiveData<PrefKey?>(null)
    val shouldShowPopupMenu = MutableLiveData(false)

    // functions
    fun handleOnTapPreferenceComponent(prefKey: PrefKey, view: View) {
        popupMenuView.value = view
        popupMenuPrefKey.value = prefKey
        shouldShowPopupMenu.value = true
    }
}