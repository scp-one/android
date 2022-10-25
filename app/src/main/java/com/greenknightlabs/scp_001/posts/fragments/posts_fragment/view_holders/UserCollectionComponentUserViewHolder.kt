package com.greenknightlabs.scp_001.posts.fragments.posts_fragment.view_holders

import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.greenknightlabs.scp_001.R
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
    fun bind(position: Int, vm: PostsFragmentViewModel) {
        val user = vm.users.value?.get(position) ?: return

        binding.user = user
        binding.listener = vm

        binding.componentUserCollectionUserImageView.load(user.avatarUrl) {
            size(140)
            transformations(CircleCropTransformation())
            crossfade(true)
            error(R.drawable.default_avatar)
        }
    }
}
