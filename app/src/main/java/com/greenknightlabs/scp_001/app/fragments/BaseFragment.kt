package com.greenknightlabs.scp_001.app.fragments

import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.greenknightlabs.scp_001.R

open class BaseFragment<T: ViewDataBinding>(
    @LayoutRes layoutId: Int
) : Fragment(layoutId), MenuProvider {
    // properties
    protected lateinit var binding: T
    protected var menu: Menu? = null

    // functions
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        configureActivityTitleAndMenu()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun configureActivityTitleAndMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.invalidateMenu()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureView(view, savedInstanceState)
    }

    protected open fun configureView(view: View, savedInstanceState: Bundle?) {
        binding = DataBindingUtil.bind(view)!!
    }

    protected open fun activityTitle(): String {
        return "Change Me"
    }

    protected open fun menuId(): Int? {
        return null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        activity?.title = activityTitle()
        menu.clear()
        menuId()?.let {
            menuInflater.inflate(it, menu)
            this.menu = menu
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }
}