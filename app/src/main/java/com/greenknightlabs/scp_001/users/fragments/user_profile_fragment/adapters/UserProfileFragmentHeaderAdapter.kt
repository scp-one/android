package com.greenknightlabs.scp_001.users.fragments.user_profile_fragment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.ComponentHeaderFragmentUserProfileBinding
import com.greenknightlabs.scp_001.databinding.ComponentPostBinding
import com.greenknightlabs.scp_001.posts.view_holders.PostComponentViewHolder
import com.greenknightlabs.scp_001.users.fragments.user_profile_fragment.UserProfileFragmentViewModel
import com.greenknightlabs.scp_001.users.fragments.user_profile_fragment.view_holders.UserProfileFragmentHeaderComponentViewHolder

class UserProfileFragmentHeaderAdapter(
    private val vm: UserProfileFragmentViewModel,
    private val kairos: Kairos,
) : RecyclerView.Adapter<UserProfileFragmentHeaderComponentViewHolder>() {
    // properties
    private lateinit var binding: ComponentHeaderFragmentUserProfileBinding

    // functions
    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserProfileFragmentHeaderComponentViewHolder {
        binding = ComponentHeaderFragmentUserProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserProfileFragmentHeaderComponentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserProfileFragmentHeaderComponentViewHolder, position: Int) {
        holder.bind(vm, kairos)
    }
}
