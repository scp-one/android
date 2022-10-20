package com.greenknightlabs.scp_001.posts.fragments.post_actions_fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.adapters.PageAdapter
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.getView
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.FragmentPostActionsBinding
import com.greenknightlabs.scp_001.posts.adapters.PostsAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostActionsFragment : BaseFragment<FragmentPostActionsBinding>(R.layout.fragment_post_actions) {
    // dependencies
    @Inject lateinit var kairos: Kairos

    // properties
    private val vm: PostActionsFragmentViewModel by viewModels()

    // functions
    override fun activityTitle(): String {
        return vm.actionType.value?.displayName() ?: ""
    }

    override fun menuId(): Int? {
        return R.menu.menu_fragment_post_actions
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        val view = activity?.getView(menuItem.itemId)
        when (menuItem.itemId) {
            R.id.menu_fragment_post_actions_sort -> vm.handleOnTapMenuSort(view)
        }

        return false
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        if (vm.adapter == null) {
            val itemsAdapter = PostsAdapter(vm, kairos)
            val pageAdapter = PageAdapter(vm)
            vm.itemsAdapter = itemsAdapter
            vm.pageAdapter = pageAdapter
            vm.adapter = ConcatAdapter(itemsAdapter, pageAdapter)
        }

        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        binding.fragmentPostActionsRecyclerView.adapter = vm.adapter!!
        binding.fragmentPostActionsRecyclerView.layoutManager = layoutManager
        binding.fragmentPostActionsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        vm.failedToLoad.observe(viewLifecycleOwner) {
            vm.pageAdapter!!.notifyItemChanged(0)
        }
    }
}
