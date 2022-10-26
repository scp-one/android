package com.greenknightlabs.scp_001.posts.fragments.post_fragment.view_holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.app.view_models.PageViewModel
import com.greenknightlabs.scp_001.comments.models.PostComment
import com.greenknightlabs.scp_001.databinding.ComponentPostCommentBinding

class PostCommentComponentViewHolder(
    private val binding: ComponentPostCommentBinding
) : RecyclerView.ViewHolder(binding.root) {
    // interfaces
    interface Listener {
        fun handleOnTapMore(comment: PostComment, view: View)
    }

    // functions
    fun bind(position: Int, vm: PageViewModel<PostComment>, listener: Listener) {
        val comment = vm.items.value!![position]

        binding.comment = comment
        binding.listener = listener
    }
}
