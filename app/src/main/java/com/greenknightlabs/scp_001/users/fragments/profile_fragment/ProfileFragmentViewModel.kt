package com.greenknightlabs.scp_001.users.fragments.profile_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import coil.Coil
import com.greenknightlabs.scp_001.BuildConfig
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.config.AppConstants
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.view_models.PageViewModel
import com.greenknightlabs.scp_001.app.fragments.appearance_fragment.AppearanceFragment
import com.greenknightlabs.scp_001.app.fragments.dependencies_fragment.DependenciesFragment
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.Stash
import com.greenknightlabs.scp_001.app.fragments.behavior_fragment.BehaviorFragment
import com.greenknightlabs.scp_001.app.fragments.pro_access_fragment.ProAccessFragment
import com.greenknightlabs.scp_001.app.fragments.tip_jar_fragment.TipJarFragment
import com.greenknightlabs.scp_001.app.util.shopkeep.Shopkeep
import com.greenknightlabs.scp_001.auth.AuthService
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.posts.fragments.local_posts_fragment.LocalPostsFragment
import com.greenknightlabs.scp_001.posts.fragments.local_posts_fragment.local_posts.getPostClassifications
import com.greenknightlabs.scp_001.posts.fragments.local_posts_fragment.local_posts.getPostOrientation
import com.greenknightlabs.scp_001.users.UsersService
import com.greenknightlabs.scp_001.users.fragments.account_fragment.AccountFragment
import com.greenknightlabs.scp_001.users.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule

@HiltViewModel
class ProfileFragmentViewModel @Inject constructor(
    private val authMan: AuthMan,
    private val usersService: UsersService,
    private val authService: AuthService,
    private val stash: Stash,
    private val navMan: NavMan,
    private val shopkeep: Shopkeep,
) : PageViewModel<Nothing>() {
    // properties
    val user = MutableLiveData<User?>(null)
    val confirmAlertText = MutableLiveData("")
    val confirmAlertAction: MutableLiveData<() -> Unit> = MutableLiveData()
    val shouldShowConfirmAlert = MutableLiveData(false)
    val shouldShowRateApp = MutableLiveData(false)
    val shouldShowShareApp = MutableLiveData(false)
    val webViewUrl = MutableLiveData<String?>(null)
    val shouldShowWebView = MutableLiveData(false)

    val canRefresh = MutableLiveData(true)
    val isRefreshing = MutableLiveData(false)

    // init
    init {
        onRefreshAction()
    }

    // functions
    fun onRefreshAction() {
        if (state.value == PageState.Fetching || canRefresh.value == false) {
            isRefreshing.value = false
            return
        }

        canRefresh.value = false
        isRefreshing.value = true
        paginate(true)
        Timer("refresh", false).schedule(5000) {
            viewModelScope.launch {
                canRefresh.value = true
            }
        }
    }

    override fun handleOnTapFailToLoad() {
        return
    }

    override fun paginate(refresh: Boolean) {
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

    fun handleOnTapLogout(activity: MainActivity?) {
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
            activity?.recreate()
        }
        shouldShowConfirmAlert.value = true
    }

    fun handleOnTapProfileHeader() {
        if (user.value == null) return

        val accountFragment = AccountFragment()
        accountFragment.user = user

        navMan.pushFragment(accountFragment, true)
    }

    fun handleOnTapAppearance() {
        navMan.pushFragment(AppearanceFragment())
    }

    fun handleOnTapBehavior() {
        navMan.pushFragment(BehaviorFragment())
    }

    fun handleOnTapProAccess() {
        navMan.pushFragment(ProAccessFragment(), true)
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
        navMan.pushFragment(TipJarFragment())
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
        val localPostsFragment = LocalPostsFragment()
        localPostsFragment.activityTitle = "About The Foundation"
        localPostsFragment.posts = listOf(getPostOrientation(), getPostClassifications())
        navMan.pushFragment(localPostsFragment, false)
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
        return shopkeep.hasSupportAccess()
    }

    fun hasEntitlementProAccess(): Boolean {
        return shopkeep.hasProAccess()
    }
}
