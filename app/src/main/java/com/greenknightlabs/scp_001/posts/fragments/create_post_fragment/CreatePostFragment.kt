package com.greenknightlabs.scp_001.posts.fragments.create_post_fragment

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
import com.greenknightlabs.scp_001.databinding.FragmentCreatePostBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreatePostFragment : BaseFragment<FragmentCreatePostBinding>(R.layout.fragment_create_post) {
    // properties
    private val vm: CreatePostFragmentViewModel by viewModels()

    // functions
    override fun activityTitle(): String {
        return "Create Post"
    }

    override fun menuId(): Int? {
        return R.menu.menu_fragment_create_post
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        val view = activity?.getView(menuItem.itemId)
        when (menuItem.itemId) {
            R.id.menu_fragment_create_post_send -> vm.handleOnTapMenuSend(view)
        }
        return false
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

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
                binding.fragmentCreatePostComponentAttachMedia.componentAttachMediaImageView.load(it.url) {
                    size(180)
                    crossfade(true)
                    error(R.drawable.ic_cancel)
                }
            }
        }

        binding.fragmentCreatePostTitleEditText.showKeyboard()
    }
}
