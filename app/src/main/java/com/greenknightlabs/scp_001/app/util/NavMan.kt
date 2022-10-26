package com.greenknightlabs.scp_001.app.util

import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.greenknightlabs.scp_001.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavMan @Inject constructor() {
    enum class NavTabs { TAB1, TAB2, TAB3, TAB4 }

    interface Listener {
        fun get(): AppCompatActivity
        fun getRootFragmentOf(tab: NavTabs): Fragment?
    }

    // external properties
    private lateinit var listener: Listener
    private lateinit var navBar: BottomNavigationView
    private lateinit var fragMan: FragmentManager
    private var actionBar: ActionBar? = null
    private var containerId: Int = 0

    // internal properties
    private val stacks = mapOf<NavTabs, MutableList<String>>(
        NavTabs.TAB1 to mutableListOf(),
        NavTabs.TAB2 to mutableListOf(),
        NavTabs.TAB3 to mutableListOf(),
        NavTabs.TAB4 to mutableListOf()
    )
    private val presentedTags = mutableSetOf<String>()

    var isLocked = false
    private var activeTab: NavTabs = NavTabs.TAB1
    private val activeFragment: Fragment?
        get() = fragMan.findFragmentByTag(stacks[activeTab]?.lastOrNull() ?: "")
    private val activeFragmentTag: String?
        get() = stacks[activeTab]?.lastOrNull()

    // functions
    fun configure(listener: Listener, navBar: BottomNavigationView, containerId: Int, hideNavBar: Boolean, defaultTab: NavTabs = NavTabs.TAB1) {
        this.listener = listener
        this.navBar = navBar
        this.fragMan = listener.get().supportFragmentManager
        this.actionBar = listener.get().supportActionBar
        this.containerId = containerId

        // has no fragments
        if ((stacks[activeTab]?.size ?: 0) == 0) {
            activeTab = defaultTab
            val initialFragment = listener.getRootFragmentOf(activeTab) ?: return
            pushFragment(initialFragment, hideNavBar)
            navBar.selectedItemId = when (defaultTab) {
                NavTabs.TAB1 -> R.id.nav_bar_tab_1
                NavTabs.TAB2 -> R.id.nav_bar_tab_2
                NavTabs.TAB3 -> R.id.nav_bar_tab_3
                NavTabs.TAB4 -> R.id.nav_bar_tab_4
            }
        } else {
            // restore state
            navBar.visibility = when (presentedTags.contains(activeFragmentTag)) {
                true -> View.GONE
                else -> View.VISIBLE
            }
        }
        isLocked = false
        configureActionBar()
    }

    fun reset() {
        val t = fragMan.beginTransaction()

        for (tab in stacks.keys) {
            val tagsToPop = stacks[tab] ?: return

            for (tag in tagsToPop) {
                val fragment = fragMan.findFragmentByTag(tag) ?: continue
                t.remove(fragment)
            }

            stacks[tab]?.clear()
        }

        t.commit()

        presentedTags.clear()
        activeTab = NavTabs.TAB1
        navBar.selectedItemId = navBar.menu.getItem(0).itemId
    }

    fun pushFragment(fragment: Fragment, hideNavBar: Boolean = false, animate: Boolean = true) {
        if (isLocked) { return }
        val activeStack = stacks[activeTab] ?: return

        val tag = activeTab.name.plus(activeStack.size)
        val t = fragMan.beginTransaction()
        if (animate) {
            t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }
        activeFragment?.let { t.detach(it) }
        t.add(containerId, fragment, tag)
        t.attach(fragment)
        t.commit()

        activeStack.add(tag)
        configureActionBar()
        if (hideNavBar) {
            navBar.visibility = View.GONE
            presentedTags.add(tag)
        } else {
            navBar.visibility = View.VISIBLE
        }
    }

    fun popFragment() {
        if (isLocked) { return }
        val activeStack = stacks[activeTab] ?: return
        if (activeStack.size <= 1) {
            when (activeTab != NavTabs.TAB1) {
                true -> navBar.selectedItemId = navBar.menu.getItem(0).itemId
                else -> listener.get().moveTaskToBack(true)
            }
            return
        }

        val poppedFragmentTag = activeFragmentTag ?: return
        val poppedFragment = activeFragment ?: return
        activeStack.remove(poppedFragmentTag)
        val previousFragment = activeFragment ?: return

        val t = fragMan.beginTransaction()
        t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        t.remove(poppedFragment)
        t.attach(previousFragment)
        t.commit()

        configureActionBar()
        presentedTags.remove(poppedFragmentTag)
        navBar.visibility = when (presentedTags.contains(previousFragment.tag)) {
            true -> View.GONE
            else -> View.VISIBLE
        }
    }

    fun switchTo(newTab: NavTabs) {
        if (newTab == activeTab) {
            return popAllFragments()
        }

        val activeFragment = activeFragment ?: return
        val newStack = stacks[newTab] ?: return
        val newFragmentTag = newStack.lastOrNull()
        val newFragment = when (newFragmentTag == null) {
            true -> listener.getRootFragmentOf(newTab)
            else -> fragMan.findFragmentByTag(newFragmentTag)
        } ?: return

        val t = fragMan.beginTransaction()
        if (newFragmentTag == null) {
            val newTag = newTab.name.plus(newStack.size)
            newStack.add(newTag)

            t.detach(activeFragment)
            t.add(containerId, newFragment, newTag)
            t.attach(newFragment)
        } else {
            t.detach(activeFragment)
            t.attach(newFragment)
        }
        t.commit()

        activeTab = newTab
        configureActionBar()
        navBar.visibility = when (presentedTags.contains(activeFragmentTag)) {
            true -> View.GONE
            else -> View.VISIBLE
        }
    }

    private fun popAllFragments() {
        if (isLocked) { return }
        val activeStack = stacks[activeTab] ?: return
        if (activeStack.size <= 1) { return }
        val visibleFragment = activeFragment ?: return

        val poppedFragmentTags = activeStack.takeLast(activeStack.size - 1)
        activeStack.removeAll(poppedFragmentTags)

        val t = fragMan.beginTransaction()
        for (tag in poppedFragmentTags) {
            val fragment = fragMan.findFragmentByTag(tag) ?: continue
            t.remove(fragment)
            presentedTags.remove(tag)
        }
        t.commit()

        val activeFragment = activeFragment ?: return
        fragMan.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            .remove(visibleFragment)
            .attach(activeFragment)
            .commit()

        configureActionBar()
        navBar.visibility = when (presentedTags.contains(activeFragmentTag)) {
            true -> View.GONE
            else -> View.VISIBLE
        }
    }

    private fun configureActionBar() {
        actionBar?.setDisplayHomeAsUpEnabled(when (stacks[activeTab]?.size ?: 0 <= 1) {
            true -> false
            else -> true
        })
    }
}
