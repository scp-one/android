package com.greenknightlabs.scp_001.users.fragments.account_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.view_models.BaseViewModel
import com.greenknightlabs.scp_001.users.UsersService
import com.greenknightlabs.scp_001.users.dtos.EditUserDto
import com.greenknightlabs.scp_001.users.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountFragmentViewModel @Inject constructor(
    private val usersService: UsersService,
    private val navMan: NavMan
) : BaseViewModel() {
    // properties
    val nickname = MutableLiveData("")
    val avatarUrl = MutableLiveData<String?>(null)
    val isLocked = MutableLiveData(false)
    val confirmAlertText = MutableLiveData("")
    val confirmAlertAction: MutableLiveData<() -> Unit> = MutableLiveData()
    val shouldShowConfirmAlert = MutableLiveData(false)

    // init
    init { }

    // functions
    fun save(user: MutableLiveData<User?>) {
        val userValue = user.value ?: return

        isLocked.value = true
        state.value = PageState.Fetching

        val dto = EditUserDto(
            nickname.value,
            avatarUrl.value,
            null,
            null
        )

        viewModelScope.launch {
            try {
                user.value = usersService.editUserById(userValue.id, dto)
                isLocked.value = false
                state.value = PageState.Idle
                toastMessage.value = "Saved."
            } catch (e: Throwable) {
                isLocked.value = false
                state.value = PageState.Idle
                toastMessage.value = e.message
            }
        }
    }

    fun handleOnTapMyProfile() {
        toastMessage.value = "Not implemented"
    }

    fun handleOnTapLikedPosts() {
        toastMessage.value = "Not implemented"
    }

    fun handleOnTapMediaCollection() {
        toastMessage.value = "Not implemented"
    }

    fun handleOnTapChangeEmail() {
        toastMessage.value = "Not implemented"
    }

    fun handleOnTapChangePassword() {
        toastMessage.value = "Not implemented"
    }

    fun handleOnTapRestorePurchases() {
        confirmAlertText.value = "Are you sure you want to do that?"
        confirmAlertAction.value = { restorePurchases() }
        shouldShowConfirmAlert.value = true
    }

    private fun restorePurchases() {
        toastMessage.value = "Not implemented"
    }

    fun handleOnTapAdvancedSettings() {
        toastMessage.value = "Not implemented"
    }
}