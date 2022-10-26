package com.greenknightlabs.scp_001.posts.fragments.post_fragment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.databinding.ComponentPostCommentBinding
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.PostFragmentViewModel
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.view_holders.PostCommentComponentViewHolder

class PostCommentsAdapter(
    private val vm: PostFragmentViewModel,
) : RecyclerView.Adapter<PostCommentComponentViewHolder>() {
    // properties
    private lateinit var binding: ComponentPostCommentBinding

    // functions
    override fun getItemCount(): Int {
        return vm.items.value?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostCommentComponentViewHolder {
        binding = ComponentPostCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostCommentComponentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostCommentComponentViewHolder, position: Int) {
        holder.bind(position, vm, vm)
    }
}
