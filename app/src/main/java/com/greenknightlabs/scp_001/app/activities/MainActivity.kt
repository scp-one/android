package com.greenknightlabs.scp_001.app.activities

import android.content.ComponentCallbacks2
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.enums.MemTrimLevel
import com.greenknightlabs.scp_001.app.resources.fonts.FontSizes
import com.greenknightlabs.scp_001.app.resources.themes.Themes
import com.greenknightlabs.scp_001.app.util.*
import com.greenknightlabs.scp_001.auth.fragments.login_fragment.LoginFragment
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.databinding.ActivityMainBinding
import com.greenknightlabs.scp_001.media.fragments.media_collection_fragment.MediaCollectionFragment
import com.greenknightlabs.scp_001.users.fragments.profile_fragment.ProfileFragment
import com.greenknightlabs.scp_001.users.fragments.profile_fragment.ProfileFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavMan.Listener, ComponentCallbacks2 {
    // dependencies
    @Inject lateinit var authMan: AuthMan
    @Inject lateinit var navMan: NavMan
    @Inject lateinit var kairos: Kairos
    @Inject lateinit var stash: Stash
    @Inject lateinit var preferences: Preferences

    // properties
    private lateinit var binding: ActivityMainBinding
    private lateinit var currentTheme: Themes
    private lateinit var currentAppFontSize: FontSizes

    // functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureView()
        configureNavMan()
    }

    private fun configureView() {
        currentTheme = preferences.theme.value!!
        currentAppFontSize = preferences.appFontSize.value!!

        setTheme(preferences.theme.value!!.resId(preferences.appFontSize.value!!))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences.theme.observe(this) {
            if (currentTheme != it) { recreate() }
        }
        preferences.appFontSize.observe(this) {
            if (currentAppFontSize != it) { recreate() }
        }
    }

    private fun configureNavMan() {
        navMan.configure(this, binding.navBar, R.id.main_container, !authMan.isLoggedIn)
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
        return when (tab) {
            NavMan.NavTabs.TAB1 -> when (!authMan.isLoggedIn) {
                true -> LoginFragment()
                else -> ProfileFragment()
            }
            NavMan.NavTabs.TAB2 -> throw NotImplementedError()
            NavMan.NavTabs.TAB3 -> throw NotImplementedError()
            NavMan.NavTabs.TAB4 -> throw NotImplementedError()
        }
    }

    // component callbacks
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        when (level) {
            TRIM_MEMORY_UI_HIDDEN -> {}
            // while app is running
            TRIM_MEMORY_RUNNING_MODERATE -> {
                kairos.trimMemory(MemTrimLevel.LOW)
            }
            TRIM_MEMORY_RUNNING_LOW -> {
                kairos.trimMemory(MemTrimLevel.MEDIUM)
            }
            TRIM_MEMORY_RUNNING_CRITICAL -> {
                kairos.trimMemory(MemTrimLevel.HIGH)
            }
            // while app is in background
            TRIM_MEMORY_BACKGROUND -> {
                kairos.trimMemory(MemTrimLevel.FULLY)
            }
            TRIM_MEMORY_MODERATE -> {
                kairos.trimMemory(MemTrimLevel.FULLY)
            }
            TRIM_MEMORY_COMPLETE -> {
                kairos.trimMemory(MemTrimLevel.FULLY)
            }
        }
    }
}
