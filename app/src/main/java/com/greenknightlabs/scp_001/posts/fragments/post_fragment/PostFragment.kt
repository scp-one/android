package com.greenknightlabs.scp_001.posts.fragments.post_fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.askConfirmation
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.extensions.pushWebView
import com.greenknightlabs.scp_001.app.extensions.screenWidth
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.databinding.FragmentPostBinding
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.adapters.PostCommentPageAdapter
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.adapters.PostCommentsAdapter
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.adapters.PostDetailsAdapter
import com.greenknightlabs.scp_001.posts.models.Post
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostFragment : BaseFragment<FragmentPostBinding>(R.layout.fragment_post) {
    // properties
    private val vm: PostFragmentViewModel by viewModels()
    var post: Post? = null

    // functions
    override fun activityTitle(): String {
        return vm.post.value?.title ?: "Post"
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        if (vm.post.value == null) {
            vm.post.value = post
        }

        if (vm.adapter == null) {
            val headerAdapter = PostDetailsAdapter(vm)
            val itemsAdapter = PostCommentsAdapter(vm)
            val pageAdapter = PostCommentPageAdapter(vm)
            vm.headerAdapter = headerAdapter
            vm.itemsAdapter = itemsAdapter
            vm.pageAdapter = pageAdapter
            vm.adapter = ConcatAdapter(headerAdapter, itemsAdapter, pageAdapter)
        }

        vm.headerAdapter?.screenWidth = activity?.screenWidth() ?: 0

        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        binding.fragmentPostRecyclerView.adapter = vm.adapter!!
        binding.fragmentPostRecyclerView.layoutManager = layoutManager
        binding.fragmentPostRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (vm.state.value != PageState.Idle) return
                if (layoutManager.findLastVisibleItemPosition() != (vm.items.value?.size ?: 0)) return
                if ((vm.items.value?.size ?: 0) < 1) return
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
        vm.shouldShowWebView.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.shouldShowWebView.value = false
                vm.webViewUrl.value?.let { url -> activity?.pushWebView(url) }
            }
        }
        vm.failedToLoad.observe(viewLifecycleOwner) {
            vm.pageAdapter!!.notifyFailedToLoad()
        }
    }
}
