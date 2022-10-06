package com.greenknightlabs.scp_001.users.fragments.advanced_account_fragment

import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.view_models.BaseViewModel
import com.greenknightlabs.scp_001.auth.fragments.delete_account_fragment.DeleteAccountFragment
import com.greenknightlabs.scp_001.users.UsersService
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