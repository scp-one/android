package com.greenknightlabs.scp_001.posts.fragments.posts_fragment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.databinding.ComponentUserCollectionUserBinding
import com.greenknightlabs.scp_001.posts.fragments.posts_fragment.PostsFragmentViewModel
import com.greenknightlabs.scp_001.posts.fragments.posts_fragment.view_holders.UserCollectionComponentUserViewHolder

class UserCollectionComponentUserAdapter(
    private val vm: PostsFragmentViewModel,
) : RecyclerView.Adapter<UserCollectionComponentUserViewHolder>() {
    // properties
    private lateinit var binding: ComponentUserCollectionUserBinding

    // functions
    override fun getItemCount(): Int {
        return vm.users.value?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCollectionComponentUserViewHolder {
        binding = ComponentUserCollectionUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserCollectionComponentUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserCollectionComponentUserViewHolder, position: Int) {
        holder.bind(position, vm)
    }
}
