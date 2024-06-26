package com.greenknightlabs.scp_001.users.fragments.user_profile_fragment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.databinding.ComponentPostBinding
import com.greenknightlabs.scp_001.posts.view_holders.PostComponentViewHolder
import com.greenknightlabs.scp_001.users.fragments.user_profile_fragment.UserProfileFragmentViewModel

class UserProfileFragmentItemsAdapter(
    private val vm: UserProfileFragmentViewModel,
) : RecyclerView.Adapter<PostComponentViewHolder>() {
    // properties
    private lateinit var binding: ComponentPostBinding
    var screenWidth = 0

    // functions
    override fun getItemCount(): Int {
        return (vm.items.value?.size ?: 0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostComponentViewHolder {
        binding = ComponentPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostComponentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostComponentViewHolder, position: Int) {
        holder.bind(position, vm, vm, screenWidth)
    }
}
