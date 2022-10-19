package com.greenknightlabs.scp_001.users.fragments.account_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.app.fragments.pro_access_fragment.ProAccessFragment
import com.greenknightlabs.scp_001.app.util.shopkeep.Shopkeep
import com.greenknightlabs.scp_001.app.util.shopkeep.standalone_products.ProAccess
import com.greenknightlabs.scp_001.auth.fragments.email_update_fragment.EmailUpdateFragment
import com.greenknightlabs.scp_001.auth.fragments.pass_update_fragment.PassUpdateFragment
import com.greenknightlabs.scp_001.media.fragments.media_collection_fragment.MediaCollectionFragment
import com.greenknightlabs.scp_001.media.models.Media
import com.greenknightlabs.scp_001.posts.fragments.post_actions_fragment.PostActionsFragment
import com.greenknightlabs.scp_001.users.UsersService
import com.greenknightlabs.scp_001.users.dtos.EditUserDto
import com.greenknightlabs.scp_001.users.fragments.advanced_account_fragment.AdvancedAccountFragment
import com.greenknightlabs.scp_001.users.fragments.user_profile_fragment.UserProfileFragment
import com.greenknightlabs.scp_001.users.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AccountFragmentViewModel @Inject constructor(
    private val usersService: UsersService,
    private val navMan: NavMan,
    private val shopkeep: Shopkeep
) : BaseViewModel(), MediaCollectionFragment.Listener {
    // properties
    var user: MutableLiveData<User?>? = null

    val nickname = MutableLiveData("")
    val avatarUrl = MutableLiveData<String?>(null)
    val isLocked = MutableLiveData(false)
    val confirmAlertText = MutableLiveData("")
    val confirmAlertAction: MutableLiveData<() -> Unit> = MutableLiveData()
    val shouldShowConfirmAlert = MutableLiveData(false)

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

    fun handleOnTapAvatar() {
        if (shopkeep.hasProAccess()) {
            val mediaCollectionFragment = MediaCollectionFragment()
            mediaCollectionFragment.listener = this
            navMan.pushFragment(mediaCollectionFragment, true)
        } else {
            navMan.pushFragment(ProAccessFragment(), true)
        }
    }

    fun handleOnTapMyProfile() {
        if (user?.value == null) return

        val userProfileFragment = UserProfileFragment()
        userProfileFragment.user = user?.value

        navMan.pushFragment(userProfileFragment, true)
    }

    fun handleOnTapLikedPosts() {
        navMan.pushFragment(PostActionsFragment(), true)
    }

    fun handleOnTapMediaCollection() {
        if (shopkeep.hasProAccess()) {
            navMan.pushFragment(MediaCollectionFragment(), true)
        } else {
            navMan.pushFragment(ProAccessFragment(), true)
        }
    }

    fun handleOnTapChangeEmail() {
        navMan.pushFragment(EmailUpdateFragment(), true)
    }

    fun handleOnTapChangePassword() {
        navMan.pushFragment(PassUpdateFragment(), true)
    }

    fun handleOnTapRestorePurchases() {
        confirmAlertText.value = "Are you sure you want to do that?"
        confirmAlertAction.value = { restorePurchases() }
        shouldShowConfirmAlert.value = true
    }

    private fun restorePurchases() {
        isLocked.value = true
        state.value = PageState.Fetching

        viewModelScope.launch {
            try {
                shopkeep.restore()
                isLocked.value = false
                state.value = PageState.Idle
                toastMessage.value = "Restored."
            } catch (e: Throwable) {
                isLocked.value = false
                state.value = PageState.Idle
                toastMessage.value = e.message
            }
        }
    }

    fun handleOnTapAdvancedSettings() {
        navMan.pushFragment(AdvancedAccountFragment(), true)
    }

    // MediaCollectionFragment.Listener
    override fun handleMediaSelected(media: Media) {
        avatarUrl.value = media.url
    }
}
