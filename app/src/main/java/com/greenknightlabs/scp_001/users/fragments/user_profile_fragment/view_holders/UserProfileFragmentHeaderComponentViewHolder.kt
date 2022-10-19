package com.greenknightlabs.scp_001.users.fragments.user_profile_fragment.view_holders

import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.ComponentHeaderFragmentUserProfileBinding
import com.greenknightlabs.scp_001.users.fragments.user_profile_fragment.UserProfileFragmentViewModel

class UserProfileFragmentHeaderComponentViewHolder(
    private val binding: ComponentHeaderFragmentUserProfileBinding
) : RecyclerView.ViewHolder(binding.root) {
    // functions
    fun bind(vm: UserProfileFragmentViewModel, kairos: Kairos) {
        binding.vm = vm

        vm.user?.avatarUrl?.let {
            kairos.load(it).scale(240, 240).default(R.drawable.default_avatar).into(binding.layoutHeaderFragmentUserProfileAvatar)
        }
    }
}
