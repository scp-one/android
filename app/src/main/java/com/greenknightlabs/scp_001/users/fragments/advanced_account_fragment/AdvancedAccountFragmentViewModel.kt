package com.greenknightlabs.scp_001.users.fragments.advanced_account_fragment

import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.auth.fragments.delete_account_fragment.DeleteAccountFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdvancedAccountFragmentViewModel @Inject constructor(
    private val navMan: NavMan,
) : BaseViewModel() {
    // properties

    // functions
    fun handleOnTapDeleteAccount() {
        navMan.pushFragment(DeleteAccountFragment(), true)
    }
}