package com.greenknightlabs.scp_001.posts.fragments.post_fragment.view_holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.databinding.ComponentLoadCommentsBinding
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.PostFragmentViewModel

class LoadCommentsComponentViewHolder(
    private val binding: ComponentLoadCommentsBinding
) : RecyclerView.ViewHolder(binding.root) {
    // interfaces
    interface Listener {
        fun handleOnTapLoadComments()
    }

    // functions
    fun bind(vm: PostFragmentViewModel, listener: Listener) {
        binding.listener = listener
        binding.root.visibility = when (vm.tappedLoadComments.value == false) {
            true -> View.VISIBLE
            else -> View.GONE
        }
    }
}
