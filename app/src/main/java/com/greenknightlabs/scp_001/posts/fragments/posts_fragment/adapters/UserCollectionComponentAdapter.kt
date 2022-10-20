package com.greenknightlabs.scp_001.posts.fragments.posts_fragment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.app.util.shopkeep.Shopkeep
import com.greenknightlabs.scp_001.databinding.ComponentUserCollectionBinding
import com.greenknightlabs.scp_001.posts.PostsViewModel
import com.greenknightlabs.scp_001.posts.fragments.posts_fragment.PostsFragmentViewModel
import com.greenknightlabs.scp_001.posts.fragments.posts_fragment.view_holders.UserCollectionComponentUserViewHolder
import com.greenknightlabs.scp_001.posts.fragments.posts_fragment.view_holders.UserCollectionComponentViewHolder

class UserCollectionComponentAdapter(
    private val vm: PostsFragmentViewModel,
    private val shopkeep: Shopkeep,
) : RecyclerView.Adapter<UserCollectionComponentViewHolder>() {
    // properties
    private lateinit var binding: ComponentUserCollectionBinding

    // functions
    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCollectionComponentViewHolder {
        binding = ComponentUserCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserCollectionComponentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserCollectionComponentViewHolder, position: Int) {
        holder.bind(vm, shopkeep)
    }
}
