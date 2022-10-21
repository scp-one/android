package com.greenknightlabs.scp_001.auth.fragments.delete_account_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.users.UsersService
import com.greenknightlabs.scp_001.users.dtos.DeleteUserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAccountFragmentViewModel @Inject constructor(
    private val usersService: UsersService,
    private val authMan: AuthMan,
    private val navMan: NavMan
) : BaseViewModel() {
    // properties
    val passw = MutableLiveData("")
    val isLocked = MutableLiveData(false)
    val shouldResetActivity = MutableLiveData(false)
    val confirmAlertText = MutableLiveData("")
    val confirmAlertAction: MutableLiveData<() -> Unit> = MutableLiveData()
    val shouldShowConfirmAlert = MutableLiveData(false)

    // functions
    fun didTapButtonDelete() {
        confirmAlertText.value = "Are you sure you want to delete your account?\nThis action is irreversible and will delete all data related to your account"
        confirmAlertAction.value = {
            deleteAccount()
        }
        shouldShowConfirmAlert.value = true
    }

    private fun deleteAccount() {
        val id = authMan.payload?.id ?: return
        val passw = passw.value ?: return

        if (passw.isEmpty()) {
            toastMessage.value = "Invalid input."
            return
        }

        val dto = DeleteUserDto(passw)

        isLocked.value = true
        state.value = PageState.Fetching

        viewModelScope.launch {
            try {
                usersService.deleteUser(id, dto)

                authMan.didLogout()
                shouldResetActivity.value = true
            } catch (e: Throwable) {
                isLocked.value = false
                state.value = PageState.Idle
                toastMessage.value = e.message
            }
        }
    }
}
