package com.greenknightlabs.scp_001.scps.fragments.scps_fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.adapters.PageAdapter
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.*
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.app.util.Preferences
import com.greenknightlabs.scp_001.databinding.FragmentScpsBinding
import com.greenknightlabs.scp_001.scps.adapters.ScpsAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ScpsFragment : BaseFragment<FragmentScpsBinding>(R.layout.fragment_scps) {
    // dependencies
    @Inject lateinit var preferences: Preferences

    // properties
    private val vm: ScpsFragmentViewModel by viewModels()

    // functions
    override fun activityTitle(): String {
        return "Archives"
    }

    override fun menuId(): Int? {
        return R.menu.menu_fragment_scps
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateMenu(menu, menuInflater)

        (menu.findItem(R.id.menu_fragment_scps_search).actionView as? SearchView)?.apply {
            this.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    activity?.hideKeyboard()
                    vm.handleOnSubmitQuery(query)
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    vm.query.value = if (query == null || query.isEmpty()) null else query
                    return false
                }
            })
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        val view = activity?.getView(menuItem.itemId)
        when (menuItem.itemId) {
            R.id.menu_fragment_scps_sort -> vm.handleOnTapMenuSort(view)
        }

        return false
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        if (vm.adapter == null) {
            val itemsAdapter = ScpsAdapter(vm, preferences)
            val pageAdapter = PageAdapter(vm)
            vm.itemsAdapter = itemsAdapter
            vm.pageAdapter = pageAdapter
            vm.adapter = ConcatAdapter(itemsAdapter, pageAdapter)
        }

        vm.itemsAdapter?.screenWidth = activity?.screenWidth() ?: 0

        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        binding.fragmentScpsRecyclerView.adapter = vm.adapter!!
        binding.fragmentScpsRecyclerView.layoutManager = layoutManager
        binding.fragmentScpsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (vm.state.value != PageState.Idle) return
                if (layoutManager.findLastVisibleItemPosition() != (vm.items.value?.size ?: 0)) return
                vm.paginate(false)
            }
        })

        vm.state.observe(viewLifecycleOwner) {
            (activity as? MainActivity)?.showProgressBar(it == PageState.Fetching)
        }
        vm.toastMessage.observe(viewLifecycleOwner) {
            if (it != null) {
                activity?.makeToast(it)
                vm.toastMessage.value = null
            }
        }
        vm.shouldShowWebView.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.shouldShowWebView.value = false
                vm.webViewUrl.value?.let { url -> activity?.pushWebView(url) }
            }
        }
        vm.failedToLoad.observe(viewLifecycleOwner) {
            vm.pageAdapter!!.notifyItemChanged(0)
        }
    }
}
