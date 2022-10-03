package com.greenknightlabs.scp_001.users.fragments.profile_fragment

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.review.ReviewManagerFactory
import com.greenknightlabs.scp_001.BuildConfig
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.enums.MemTrimLevel
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.objects.ListOption
import com.greenknightlabs.scp_001.app.objects.ListOptionSection
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.Stash
import com.greenknightlabs.scp_001.app.view_models.BaseViewModel
import com.greenknightlabs.scp_001.app.view_models.PageViewModel
import com.greenknightlabs.scp_001.auth.AuthService
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.users.UsersService
import com.greenknightlabs.scp_001.users.fragments.account_fragment.AccountFragment
import com.greenknightlabs.scp_001.users.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileFragmentViewModel @Inject constructor(
    private val authMan: AuthMan,
    private val usersService: UsersService,
    private val authService: AuthService,
    private val stash: Stash,
    private val navMan: NavMan,
) : BaseViewModel() {
    // properties
    val user = MutableLiveData<User?>(null)
    val isRefreshing = MutableLiveData(false)
    val confirmAlertText = MutableLiveData("")
    val confirmAlertAction: MutableLiveData<() -> Unit> = MutableLiveData()
    val shouldShowConfirmAlert = MutableLiveData(false)
    val shouldShowRateApp = MutableLiveData(false)
    val shouldShowFeedback = MutableLiveData(false)
    val shouldShowPrivacyPolicy = MutableLiveData(false)
    val shouldShowTermsOfService = MutableLiveData(false)

    // init
    init {
        paginate(true)
    }

    // functions
    fun paginate(refresh: Boolean) {
        state.value = PageState.Fetching

        viewModelScope.launch {
            try {
                user.value = usersService.getUserFromRequest()
                state.value = PageState.Idle
                isRefreshing.value = false
            } catch (e: Throwable) {
                state.value = PageState.Idle
                toastMessage.value = e.message
                isRefreshing.value = false
            }
        }
    }

    fun onRefreshAction() {
        if (state.value == PageState.Fetching) {
            isRefreshing.value = false
            return
        }

        isRefreshing.value = true
        paginate(true)
    }

    fun handleOnTapLogout(context: Context?, activity: MainActivity?) {
        confirmAlertText.value = "Are you sure you want to logout?"
        confirmAlertAction.value = {
            authMan.accessInfo?.let { accessInfo ->
                viewModelScope.launch {
                    try {
                        authService.logout(accessInfo)
                    } catch (e: Throwable) {
                        Timber.e(e)
                    }
                }
            }

            authMan.didLogout()
            navMan.reset()
            context?.startActivity(Intent(context, MainActivity::class.java))
            activity?.finish()
        }
        shouldShowConfirmAlert.value = true
    }

    fun handleOnTapProfileHeader() {
        navMan.pushFragment(AccountFragment(user), true)
    }

    fun handleOnTapAppearance() {
        toastMessage.value = "appearance"
    }

    fun handleOnTapBehavior() {
        toastMessage.value = "behavior"
    }

    fun handleOnTapProAccess() {
        toastMessage.value = "Not implemented"
    }

    fun handleOnTapClearCache() {
        confirmAlertText.value = "Are you sure you want to do that?"
        confirmAlertAction.value = {
            toastMessage.value = "Not implemented"
        }
        shouldShowConfirmAlert.value = true
    }

    fun handleOnTapTipJar() {
        toastMessage.value = "Not implemented"
    }

    fun handleOnTapRateThisApp() {
        toastMessage.value = "Not implemented"
    }

    fun handleOnTapSendFeedback() {
        toastMessage.value = "Not implemented"
    }

    fun handleOnTapShareThisApp() {
        toastMessage.value = "Not implemented"
    }

    fun handleOnTapAboutTheFoundation() {
        toastMessage.value = "Not implemented"
    }

    fun handleOnTapPrivacyPolicy() {
        shouldShowPrivacyPolicy.value = true
    }

    fun handleOnTapTermsOfService() {
        shouldShowTermsOfService.value = true
    }

    fun handleOnTapLicenses() {
        toastMessage.value = "Not implemented"
//        navMan.pushFragment(DependenciesFragment())
    }

    fun hasEntitlementSupporter(): Boolean {
        return false
    }

    fun hasEntitlementProAccess(): Boolean {
        return false
    }

    fun getAppVersionNumber(): String {
        return BuildConfig.VERSION_NAME
    }
}