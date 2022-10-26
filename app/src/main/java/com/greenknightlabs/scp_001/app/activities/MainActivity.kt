package com.greenknightlabs.scp_001.app.activities

import android.app.ActivityManager
import android.content.ComponentCallbacks2
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import coil.Coil
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.enums.DefaultAppLaunchTab
import com.greenknightlabs.scp_001.app.enums.MemTrimLevel
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.resources.fonts.FontSizes
import com.greenknightlabs.scp_001.app.resources.themes.Themes
import com.greenknightlabs.scp_001.app.util.*
import com.greenknightlabs.scp_001.auth.fragments.login_fragment.LoginFragment
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.databinding.ActivityMainBinding
import com.greenknightlabs.scp_001.media.fragments.media_collection_fragment.MediaCollectionFragment
import com.greenknightlabs.scp_001.posts.fragments.posts_fragment.PostsFragment
import com.greenknightlabs.scp_001.scps.enums.ScpLoadImages
import com.greenknightlabs.scp_001.scps.enums.ScpSortField
import com.greenknightlabs.scp_001.scps.enums.ScpSortOrder
import com.greenknightlabs.scp_001.scps.fragments.scp_actions_fragment.ScpActionsFragment
import com.greenknightlabs.scp_001.scps.fragments.scp_actions_fragment.ScpActionsFragmentViewModel
import com.greenknightlabs.scp_001.scps.fragments.scps_fragment.ScpsFragment
import com.greenknightlabs.scp_001.users.fragments.profile_fragment.ProfileFragment
import com.greenknightlabs.scp_001.users.fragments.profile_fragment.ProfileFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavMan.Listener, ComponentCallbacks2 {
    // dependencies
    @Inject lateinit var authMan: AuthMan
    @Inject lateinit var navMan: NavMan
    @Inject lateinit var stash: Stash
    @Inject lateinit var preferences: Preferences

    // properties
    private lateinit var binding: ActivityMainBinding

    private lateinit var currentTheme: Themes
    private lateinit var currentAppFontSize: FontSizes
    private lateinit var currentScpFontSize: FontSizes

    private lateinit var currentDefaultAppLaunchTab: DefaultAppLaunchTab
    private lateinit var currentDefaultScpSortField: ScpSortField
    private lateinit var currentDefaultScpSortOrder: ScpSortOrder
    private lateinit var currentLoadScpImages: ScpLoadImages

    // functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureView()
        configureNavMan()
    }

    private fun configureView() {
        setTheme(preferences.theme.value!!.resId(preferences.appFontSize.value!!))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTheme = preferences.theme.value!!
        currentAppFontSize = preferences.appFontSize.value!!
        currentScpFontSize = preferences.scpFontSize.value!!

        currentDefaultAppLaunchTab = preferences.defaultLaunchTab.value!!
        currentDefaultScpSortField = preferences.defaultScpSortField.value!!
        currentDefaultScpSortOrder = preferences.defaultScpSortOrder.value!!
        currentLoadScpImages = preferences.loadScpImages.value!!

        preferences.theme.observe(this) {
            if (currentTheme != it) { recreate() }
        }
        preferences.appFontSize.observe(this) {
            if (currentAppFontSize != it) { recreate() }
        }
        preferences.scpFontSize.observe(this) {
            if (currentScpFontSize != it) { recreate() }
        }

        preferences.defaultLaunchTab.observe(this) {
            if (currentDefaultAppLaunchTab != it) { recreate() }
        }
        preferences.defaultScpSortField.observe(this) {
            if (currentDefaultScpSortField != it) { recreate() }
        }
        preferences.defaultScpSortOrder.observe(this) {
            if (currentDefaultScpSortOrder != it) { recreate() }
        }
        preferences.loadScpImages.observe(this) {
            if (currentLoadScpImages != it) { recreate() }
        }
    }

    private fun configureNavMan() {
        val defaultTab: NavMan.NavTabs = when (!authMan.isLoggedIn) {
            true -> NavMan.NavTabs.TAB1
            else -> when (preferences.defaultLaunchTab.value) {
                DefaultAppLaunchTab.ARCHIVES -> NavMan.NavTabs.TAB1
                DefaultAppLaunchTab.POSTS -> NavMan.NavTabs.TAB2
                DefaultAppLaunchTab.BOOKMARKS -> NavMan.NavTabs.TAB3
                DefaultAppLaunchTab.PROFILE -> NavMan.NavTabs.TAB4
                else -> NavMan.NavTabs.TAB1
            }
        }

        navMan.configure(this, binding.navBar, R.id.main_container, !authMan.isLoggedIn, defaultTab)
        binding.navBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_bar_tab_1 -> navMan.switchTo(NavMan.NavTabs.TAB1)
                R.id.nav_bar_tab_2 -> navMan.switchTo(NavMan.NavTabs.TAB2)
                R.id.nav_bar_tab_3 -> navMan.switchTo(NavMan.NavTabs.TAB3)
                R.id.nav_bar_tab_4 -> navMan.switchTo(NavMan.NavTabs.TAB4)
            }
            true
        }
    }

    override fun onBackPressed() {
        navMan.popFragment()
    }

    override fun onSupportNavigateUp(): Boolean {
        navMan.popFragment()
        return true
    }

    fun lockUI(lock: Boolean) {
        navMan.isLocked = lock
        val flag = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        if (lock) window.setFlags(flag, flag) else window.clearFlags(flag)
    }

    fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    // nav man listener
    override fun get(): AppCompatActivity {
        return this
    }

    override fun getRootFragmentOf(tab: NavMan.NavTabs): Fragment? {
        return when (authMan.isLoggedIn) {
            true -> when (tab) {
                NavMan.NavTabs.TAB1 -> ScpsFragment()
                NavMan.NavTabs.TAB2 -> PostsFragment()
                NavMan.NavTabs.TAB3 -> ScpActionsFragment()
                NavMan.NavTabs.TAB4 -> ProfileFragment()
            }
            else -> LoginFragment()
        }
    }

    fun getAvailableMemory(): ActivityManager.MemoryInfo {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return ActivityManager.MemoryInfo().also { memoryInfo ->
            activityManager.getMemoryInfo(memoryInfo)
        }
    }

    // component callbacks
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Coil.imageLoader(this).memoryCache?.trimMemory(level)
    }
}
