package com.greenknightlabs.scp_001.users.fragments.profile_fragment

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.BuildConfig
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.config.AppConstants
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.fragments.appearance_fragment.AppearanceFragment
import com.greenknightlabs.scp_001.app.fragments.dependencies_fragment.DependenciesFragment
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.Stash
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseViewModel
import com.greenknightlabs.scp_001.app.fragments.behavior_fragment.BehaviorFragment
import com.greenknightlabs.scp_001.auth.AuthService
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.users.UsersService
import com.greenknightlabs.scp_001.users.fragments.account_fragment.AccountFragment
import com.greenknightlabs.scp_001.users.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val shouldShowShareApp = MutableLiveData(false)
    val webViewUrl = MutableLiveData("")
    val shouldShowWebView = MutableLiveData(false)

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
        if (user.value == null) return

        navMan.pushFragment(AccountFragment(), true)
    }

    fun handleOnTapAppearance() {
        navMan.pushFragment(AppearanceFragment())
    }

    fun handleOnTapBehavior() {
        navMan.pushFragment(BehaviorFragment())
    }

    fun handleOnTapProAccess() {
        toastMessage.value = "Not implemented"
    }

    fun handleOnTapClearCache() {
        confirmAlertText.value = "Are you sure you want to do that?"
        confirmAlertAction.value = {
            viewModelScope.launch {
                stash.empty()
                toastMessage.value = "Cleared."
            }
        }
        shouldShowConfirmAlert.value = true
    }

    fun handleOnTapTipJar() {
        toastMessage.value = "Not implemented"
    }

    fun handleOnTapRateThisApp() {
        shouldShowRateApp.value = true
    }

    fun handleOnTapSendFeedback() {
        webViewUrl.value = AppConstants.URL_FEEDBACK
        shouldShowWebView.value = true
    }

    fun handleOnTapShareThisApp() {
        shouldShowShareApp.value = true
    }

    fun handleOnTapAboutTheFoundation() {
        toastMessage.value = "Not implemented"
    }

    fun handleOnTapPrivacyPolicy() {
        webViewUrl.value = AppConstants.URL_PRIVACY_POLICY
        shouldShowWebView.value = true
    }

    fun handleOnTapTermsOfService() {
        webViewUrl.value = AppConstants.URL_TERMS_OF_SERVICE
        shouldShowWebView.value = true
    }

    fun handleOnTapLicenses() {
        navMan.pushFragment(DependenciesFragment())
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