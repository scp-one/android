package com.greenknightlabs.scp_001.auth.fragments.login_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.auth.AuthService
import com.greenknightlabs.scp_001.auth.dtos.LoginDto
import com.greenknightlabs.scp_001.auth.fragments.pass_update_fragment.PassUpdateFragment
import com.greenknightlabs.scp_001.auth.fragments.register_fragment.RegisterFragment
import com.greenknightlabs.scp_001.auth.util.AuthMan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginFragmentViewModel @Inject constructor(
    private val authService: AuthService,
    private val authMan: AuthMan,
    private val navMan: NavMan,
) : BaseViewModel() {
    // properties
    val email = MutableLiveData("")
    val passw = MutableLiveData("")
    val isLocked = MutableLiveData(false)
    val shouldResetActivity = MutableLiveData(false)

    // functions
    fun didTapButtonForgotPassword() {
        navMan.pushFragment(PassUpdateFragment(), true)
    }

    fun didTapButtonRegister() {
        navMan.pushFragment(RegisterFragment(), true)
    }

    fun didTapButtonLogin() {
        val email = email.value ?: return
        val passw = passw.value ?: return

        if (email.isEmpty() || passw.isEmpty()) {
            toastMessage.value = "Invalid input."
            return
        }

        val dto = LoginDto(null, email, passw)

        isLocked.value = true
        state.value = PageState.Fetching

        viewModelScope.launch {
            try {
                val accessInfo = authService.login(dto)
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
