package com.mirenzen.scp_001.app.util

import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
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

    var isLocked = false
    private var activeTab: NavTabs = NavTabs.TAB1
    private val activeFragment: Fragment?
        get() = fragMan.findFragmentByTag(stacks[activeTab]?.lastOrNull() ?: "")
    private val activeFragmentTag: String?
        get() = stacks[activeTab]?.lastOrNull()

    // functions
    fun configure(listener: Listener, navBar: BottomNavigationView, containerId: Int) {
        this.listener = listener
        this.navBar = navBar
        this.fragMan = listener.get().supportFragmentManager
        this.actionBar = listener.get().supportActionBar
        this.containerId = containerId
        if (stacks[activeTab]?.size ?: 0 == 0) {
            val initialFragment = listener.getRootFragmentOf(activeTab) ?: return
            pushFragment(initialFragment, false)
        }
        isLocked = false
    }

    fun reset() {
        for (tab in stacks.keys) {
            stacks[tab]?.clear()
        }
        activeTab = NavTabs.TAB1
        navBar.selectedItemId = navBar.menu.getItem(0).itemId
    }

    fun pushFragment(fragment: Fragment, animate: Boolean = true) {
        if (isLocked) { return }
        val activeStack = stacks[activeTab] ?: return

        val tag = activeTab.name.plus(activeStack.size)
        val t = fragMan.beginTransaction()
        if (animate) {
            t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }
        activeFragment?.let { t.hide(it) }
        t.add(containerId, fragment, tag)
        t.show(fragment)
        t.commit()

        activeStack.add(tag)
        configureActionBar()
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
        t.show(previousFragment)
        t.commit()

        configureActionBar()
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

            t.hide(activeFragment)
            t.add(containerId, newFragment, newTag)
            t.show(newFragment)
        } else {
            t.hide(activeFragment)
            t.show(newFragment)
        }
        t.commit()

        activeTab = newTab
        configureActionBar()
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
        }
        t.commit()

        val activeFragment = activeFragment ?: return
        fragMan.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            .remove(visibleFragment)
            .show(activeFragment)
            .commit()

        configureActionBar()
    }

    private fun configureActionBar() {
        actionBar?.setDisplayHomeAsUpEnabled(when (stacks[activeTab]?.size ?: 0 <= 1) {
            true -> false
            else -> true
        })
    }
}