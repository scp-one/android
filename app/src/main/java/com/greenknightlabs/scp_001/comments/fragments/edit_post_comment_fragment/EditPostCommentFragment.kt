package com.greenknightlabs.scp_001.comments.fragments.edit_post_comment_fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.extensions.showKeyboard
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.comments.models.PostComment
import com.greenknightlabs.scp_001.databinding.FragmentEditPostCommentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPostCommentFragment : BaseFragment<FragmentEditPostCommentBinding>(R.layout.fragment_edit_post_comment) {
    // properties
    private val vm: EditPostCommentFragmentViewModel by viewModels()
    var comment: PostComment? = null

    // functions
    override fun activityTitle(): String {
        return "Edit Comment"
    }

    override fun menuId(): Int? {
        return R.menu.menu_fragment_edit_post_comment
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_fragment_edit_post_comment_edit -> vm.handleOnTapEdit()
        }
        return false
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        if (vm.comment == null) {
            restorePostComment()
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
        vm.content.observe(viewLifecycleOwner) {
            vm.charactersLeft.value = vm.calculateCharactersLeft().toString()
        }

        binding.fragmentEditPostCommentEditText.showKeyboard()
    }

    private fun restorePostComment() {
        val comment = comment ?: return
        vm.comment = comment
        vm.content.value = comment.content
    }
}
