package com.greenknightlabs.scp_001.posts.fragments.posts_fragment.view_holders

import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.ComponentUserCollectionUserBinding
import com.greenknightlabs.scp_001.posts.PostsViewModel
import com.greenknightlabs.scp_001.posts.fragments.posts_fragment.PostsFragmentViewModel
import com.greenknightlabs.scp_001.users.models.User

class UserCollectionComponentUserViewHolder(
    private val binding: ComponentUserCollectionUserBinding
) : RecyclerView.ViewHolder(binding.root) {
    // interfaces
    interface Listener {
        fun handleOnTapUser(user: User)
    }

    // functions
    fun bind(position: Int, vm: PostsFragmentViewModel, kairos: Kairos) {
        val user = vm.users.value?.get(position) ?: return

        binding.user = user
        binding.listener = vm

        kairos.load(user.avatarUrl)
            .scale(140, 140)
            .default(R.drawable.default_avatar)
            .into(binding.componentUserCollectionUserImageView)
    }
}
