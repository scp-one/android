package com.greenknightlabs.scp_001.posts.fragments.edit_post_fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import coil.load
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.getView
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.extensions.showKeyboard
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.databinding.FragmentEditPostBinding
import com.greenknightlabs.scp_001.media.models.Media
import com.greenknightlabs.scp_001.posts.models.Post
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPostFragment : BaseFragment<FragmentEditPostBinding>(R.layout.fragment_edit_post) {
    // properties
    private val vm: EditPostFragmentViewModel by viewModels()
    var post: Post? = null

    // functions
    override fun activityTitle(): String {
        return "Edit Post"
    }

    override fun menuId(): Int? {
        return R.menu.menu_fragment_edit_post
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        val view = activity?.getView(menuItem.itemId)
        when (menuItem.itemId) {
            R.id.menu_fragment_edit_post_edit -> vm.handleOnTapMenuEdit(view)
        }
        return false
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        if (vm.post.value == null) {
            restorePost()
        }

        vm.isLocked.observe(viewLifecycleOwner) {
            (activity as? MainActivity)?.lockUI(it)
        }
        vm.state.observe(viewLifecycleOwner) {
            (activity as? MainActivity)?.showProgressBar(it == PageState.Fetching)
        }
        vm.toastMessage.observe(viewLifecycleOwner) {
            if (it != null) {
                activity?.makeToast(it)
                vm.toastMessage.value = null
            }
        }
        vm.media.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.fragmentEditPostComponentAttachMedia.componentAttachMediaImageView.load(it.url) {
                    size(180)
                    crossfade(true)
                    error(R.drawable.ic_cancel)
                }
            }
        }

        binding.fragmentEditPostTitleEditText.showKeyboard()
    }

    private fun restorePost() {
        val post = post ?: return
        vm.post.value = post
        vm.title.value = post.title
        vm.content.value = post.content
        post.media?.let { postMedia ->
            vm.media.value = Media(
                "",
                post.user,
                postMedia.url,
                postMedia.width,
                postMedia.height,
                "",
                ""
            )
        }
    }
}
