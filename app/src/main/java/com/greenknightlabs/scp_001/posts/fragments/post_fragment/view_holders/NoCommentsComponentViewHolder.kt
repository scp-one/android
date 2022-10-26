package com.greenknightlabs.scp_001.posts.fragments.post_fragment.view_holders

import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.databinding.ComponentNoCommentsBinding
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.PostFragmentViewModel

class NoCommentsComponentViewHolder(
    private val binding: ComponentNoCommentsBinding
) : RecyclerView.ViewHolder(binding.root) {
    // functions
    fun bind(vm: PostFragmentViewModel) {
        binding.vm = vm
    }
}
