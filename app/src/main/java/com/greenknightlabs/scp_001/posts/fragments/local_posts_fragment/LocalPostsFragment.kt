package com.greenknightlabs.scp_001.posts.fragments.local_posts_fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.config.AppConstants
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.extensions.pushWebView
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.databinding.ComponentLocalPostContentBinding
import com.greenknightlabs.scp_001.databinding.ComponentLocalPostTitleBinding
import com.greenknightlabs.scp_001.databinding.FragmentLocalPostsBinding
import com.greenknightlabs.scp_001.posts.models.Post
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration

@AndroidEntryPoint
class LocalPostsFragment : BaseFragment<FragmentLocalPostsBinding>(R.layout.fragment_local_posts) {
    // properties
    private val vm: LocalPostsFragmentViewModel by viewModels()
    var activityTitle: String? = null
    var posts: List<Post>? = null

    override fun activityTitle(): String {
        return vm.activityTitle.value ?: ""
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        if (vm.activityTitle.value == null) {
            vm.activityTitle.value = activityTitle
        }
        if (vm.posts.value == null) {
            vm.posts.value = posts
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

        val markwon = Markwon.builder(requireContext())
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                    builder.linkResolver { _, link ->
                        if (link.startsWith("http")) {
                            vm.webViewUrl.value = link
                            vm.shouldShowWebView.value = true
                        } else {
                            vm.webViewUrl.value = "${AppConstants.WIKI_URL}$link"
                            vm.shouldShowWebView.value = true
                        }
                    }

                    super.configureConfiguration(builder)
                }
            })
            .build()

        renderPosts(markwon, binding.fragmentLocalPostsContainer)
    }

    private fun renderPosts(markwon: Markwon, container: LinearLayoutCompat) {
        val posts = vm.posts.value ?: return

        for (post in posts) {
            if (post.title.isNotEmpty()) {
                val postTitleBinding: ComponentLocalPostTitleBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_local_post_title, container, false)

                postTitleBinding.componentLocalPostTitleTextView.text = post.title
                container.addView(postTitleBinding.root)
            }

            val postContentBinding: ComponentLocalPostContentBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_local_post_content, container, false)

            markwon.setMarkdown(postContentBinding.componentLocalPostContentTextView, post.content)
            container.addView(postContentBinding.root)
        }
    }
}
