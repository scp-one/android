package com.greenknightlabs.scp_001.posts.fragments.post_fragment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.databinding.ComponentPostDetailsBinding
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.PostFragmentViewModel
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.view_holders.PostDetailsComponentViewHolder

class PostDetailsAdapter(
    private val vm: PostFragmentViewModel,
) : RecyclerView.Adapter<PostDetailsComponentViewHolder>() {
    // properties
    private lateinit var binding: ComponentPostDetailsBinding
    var screenWidth = 0

    // functions
    override fun getItemCount(): Int {
        return if (vm.post.value == null) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostDetailsComponentViewHolder {
        binding = ComponentPostDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostDetailsComponentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostDetailsComponentViewHolder, position: Int) {
        holder.bind(vm, screenWidth)
    }
}
