package com.greenknightlabs.scp_001.posts.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.databinding.ComponentPostBinding
import com.greenknightlabs.scp_001.posts.PostsViewModel
import com.greenknightlabs.scp_001.posts.fragments.posts_fragment.PostsFragmentViewModel
import com.greenknightlabs.scp_001.posts.view_holders.PostComponentViewHolder

class PostsAdapter(
    private val vm: PostsViewModel,
) : RecyclerView.Adapter<PostComponentViewHolder>() {
    // properties
    private lateinit var binding: ComponentPostBinding
    var screenWidth = 0

    // functions
    override fun getItemCount(): Int {
        return vm.items.value?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostComponentViewHolder {
        binding = ComponentPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostComponentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostComponentViewHolder, position: Int) {
        holder.bind(position, vm, vm, screenWidth)
    }
}
