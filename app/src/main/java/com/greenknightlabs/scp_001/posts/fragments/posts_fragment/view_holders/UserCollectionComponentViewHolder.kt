package com.greenknightlabs.scp_001.posts.fragments.posts_fragment.view_holders

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.util.shopkeep.Shopkeep
import com.greenknightlabs.scp_001.databinding.ComponentUserCollectionBinding
import com.greenknightlabs.scp_001.databinding.ComponentUserCollectionUserBinding
import com.greenknightlabs.scp_001.posts.fragments.posts_fragment.PostsFragmentViewModel
import com.greenknightlabs.scp_001.users.models.User

class UserCollectionComponentViewHolder(
    private val binding: ComponentUserCollectionBinding
) : RecyclerView.ViewHolder(binding.root) {
    // functions
    fun bind(vm: PostsFragmentViewModel, shopkeep: Shopkeep) {
        if (!shopkeep.hasProAccess()) {
            binding.root.visibility = View.GONE
        } else {
            val layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.HORIZONTAL, false)

            binding.componentUserCollectionRecyclerView.adapter = vm.headerSubAdapter!!
            binding.componentUserCollectionRecyclerView.layoutManager = layoutManager
        }
    }
}
