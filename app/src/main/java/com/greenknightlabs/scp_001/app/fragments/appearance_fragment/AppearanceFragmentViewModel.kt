package com.greenknightlabs.scp_001.app.fragments.appearance_fragment

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.Preferences
import com.greenknightlabs.scp_001.app.util.preferences.enums.PrefKey
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppearanceFragmentViewModel @Inject constructor(
    val preferences: Preferences,
    private val navMan: NavMan
) : BaseViewModel() {
    // functions
    fun handleOnTapPreferenceComponent(prefKey: PrefKey, view: View) {
        view.makePopupMenu(prefKey.displayNames()) { index ->
            preferences.set(prefKey, prefKey.rawValues()[index])
        }
    }
}