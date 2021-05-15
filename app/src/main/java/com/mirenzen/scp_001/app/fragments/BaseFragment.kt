package com.mirenzen.scp_001.app.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

open class BaseFragment<T: ViewDataBinding>(@LayoutRes layoutId: Int) : Fragment(layoutId) {
    // properties
    protected lateinit var binding: T

    private var activityMenu: Menu? = null

    // local functions
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.title = activityTitle()
        activityMenu = menu
        menu.clear()
        menuId()?.let { inflater.inflate(it, menu) }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureView(view, savedInstanceState)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) { return }
        activityMenu?.let { menu ->
            activity?.menuInflater?.let { inflater ->
                onCreateOptionsMenu(menu, inflater)
            }
        }
    }

    protected open fun configureView(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.bind(view)!!
    }

    protected open fun activityTitle(): String {
        return "Change Me"
    }

    protected open fun menuId(): Int? {
        return null
    }
}