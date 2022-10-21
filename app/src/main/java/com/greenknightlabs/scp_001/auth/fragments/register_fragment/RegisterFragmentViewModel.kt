package com.greenknightlabs.scp_001.auth.fragments.register_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.auth.AuthService
import com.greenknightlabs.scp_001.auth.dtos.RegisterDto
import com.greenknightlabs.scp_001.auth.util.AuthMan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterFragmentViewModel @Inject constructor(
    private val authService: AuthService,
    private val authMan: AuthMan,
    private val navMan: NavMan,
) : BaseViewModel() {
    // properties
    val usern = MutableLiveData("")
    val email = MutableLiveData("")
    val passw = MutableLiveData("")
    val isLocked = MutableLiveData(false)
    val shouldPresentTermsAndConditions = MutableLiveData(false)
    val shouldResetActivity = MutableLiveData(false)

    // functions
    fun didTapButtonTermsAndConditions() {
        shouldPresentTermsAndConditions.value = true
    }

    fun didTapButtonRegister() {
        val usern = usern.value ?: return
        val email = email.value ?: return
        val passw = passw.value ?: return

        if (usern.isEmpty() || email.isEmpty() || passw.isEmpty()) {
            toastMessage.value = "Invalid input."
            return
        }

        val dto = RegisterDto(usern, email, passw)

        isLocked.value = true
        state.value = PageState.Fetching

        viewModelScope.launch {
            try {
                val accessInfo = authService.register(dto)
                isLocked.value = false
                state.value = PageState.Idle
                authMan.didLogin(accessInfo)
                navMan.reset()
                shouldResetActivity.value = true
            } catch (e: Throwable) {
                isLocked.value = false
                state.value = PageState.Idle
                toastMessage.value = e.message
            }
        }
    }
}
