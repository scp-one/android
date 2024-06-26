package com.greenknightlabs.scp_001.auth.fragments.email_update_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.App
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.auth.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailUpdateFragmentViewModel @Inject constructor(
    private val authService: AuthService,
    private val navMan: NavMan,
) : BaseViewModel() {
    // properties
    val email = MutableLiveData("")
    val isLocked = MutableLiveData(false)

    // functions
    fun didTapButtonRequest() {
        val email = email.value ?: return

        if (email.isEmpty()) {
            toastMessage.value = "Invalid input."
            return
        }

        isLocked.value = true
        state.value = PageState.Fetching

        viewModelScope.launch {
            try {
                authService.getEmailUpdateMail(email)
                isLocked.value = false
                state.value = PageState.Idle
                toastMessage.value = App.context.getString(R.string.alert_email_update_sent)
                navMan.popFragment()
            } catch (e: Throwable) {
                isLocked.value = false
                state.value = PageState.Idle
                toastMessage.value = e.message
            }
        }
    }
}
