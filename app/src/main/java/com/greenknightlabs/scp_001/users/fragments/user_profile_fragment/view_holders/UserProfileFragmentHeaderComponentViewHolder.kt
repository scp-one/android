package com.greenknightlabs.scp_001.users.fragments.user_profile_fragment.view_holders

import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.databinding.ComponentHeaderFragmentUserProfileBinding
import com.greenknightlabs.scp_001.users.fragments.user_profile_fragment.UserProfileFragmentViewModel

class UserProfileFragmentHeaderComponentViewHolder(
    private val binding: ComponentHeaderFragmentUserProfileBinding
) : RecyclerView.ViewHolder(binding.root) {
    // functions
    fun bind(vm: UserProfileFragmentViewModel) {
        binding.vm = vm
        binding.layoutHeaderFragmentUserProfileAvatar.load(vm.user?.avatarUrl) {
            size(240)
            transformations(CircleCropTransformation())
            crossfade(true)
            error(R.drawable.default_avatar)
        }
    }
}
