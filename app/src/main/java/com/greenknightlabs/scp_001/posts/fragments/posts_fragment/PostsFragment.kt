package com.greenknightlabs.scp_001.posts.fragments.posts_fragment

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
import com.greenknightlabs.scp_001.app.extensions.askConfirmation
import com.greenknightlabs.scp_001.app.extensions.getView
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.extensions.screenWidth
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.app.util.shopkeep.Shopkeep
import com.greenknightlabs.scp_001.databinding.FragmentPostsBinding
import com.greenknightlabs.scp_001.posts.adapters.PostsAdapter
import com.greenknightlabs.scp_001.posts.fragments.posts_fragment.adapters.UserCollectionComponentAdapter
import com.greenknightlabs.scp_001.posts.fragments.posts_fragment.adapters.UserCollectionComponentUserAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostsFragment : BaseFragment<FragmentPostsBinding>(R.layout.fragment_posts) {
    // dependencies
    @Inject lateinit var kairos: Kairos
    @Inject lateinit var shopkeep: Shopkeep

    // properties
    private val vm: PostsFragmentViewModel by viewModels()

    // functions
    override fun activityTitle(): String {
        return "Posts"
    }

    override fun menuId(): Int? {
        return R.menu.menu_fragment_posts
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        val view = activity?.getView(menuItem.itemId)
        when (menuItem.itemId) {
            R.id.menu_fragment_posts_sort -> vm.handleOnTapMenuSort(view)
            R.id.menu_fragment_posts_post -> vm.handleOnTapMenuPost()
        }

        return false
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        if (vm.adapter == null) {
            val headerAdapter = UserCollectionComponentAdapter(vm, shopkeep)
            val headerSubAdapter = UserCollectionComponentUserAdapter(vm, kairos)
            val itemsAdapter = PostsAdapter(vm, kairos)
            val pageAdapter = PageAdapter(vm)
            vm.headerAdapter = headerAdapter
            vm.headerSubAdapter = headerSubAdapter
            vm.itemsAdapter = itemsAdapter
            vm.pageAdapter = pageAdapter
            vm.adapter = ConcatAdapter(headerAdapter, itemsAdapter, pageAdapter)
        }

        vm.itemsAdapter?.screenWidth = activity?.screenWidth() ?: 0

        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        binding.fragmentPostsRecyclerView.adapter = vm.adapter!!
        binding.fragmentPostsRecyclerView.layoutManager = layoutManager
        binding.fragmentPostsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        vm.shouldShowConfirmAlert.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.shouldShowConfirmAlert.value = false
                activity?.askConfirmation { vm.confirmAlertAction.value?.invoke() }
            }
        }
        vm.failedToLoad.observe(viewLifecycleOwner) {
            vm.pageAdapter!!.notifyItemChanged(0)
        }
    }
}
