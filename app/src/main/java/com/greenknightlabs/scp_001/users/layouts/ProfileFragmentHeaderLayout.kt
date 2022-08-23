package com.greenknightlabs.scp_001.users.layouts

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.extensions.setTextAsync
import com.greenknightlabs.scp_001.app.interfaces.BindableView
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.LayoutHeaderFragmentProfileBinding
import com.greenknightlabs.scp_001.users.models.User

class ProfileFragmentHeaderLayout(
    val binding: LayoutHeaderFragmentProfileBinding,
    private val listener: Listener?,
    private val kairos: Kairos
) : RecyclerView.ViewHolder(binding.root), BindableView<User?> {
    enum class EventType {
        ContainerTap
    }

    interface Listener {
        fun handleLayoutEvent(event: EventType, index: Int, view: View?)
    }

    companion object {
        fun layoutId(): Int {
            return R.layout.layout_header_fragment_profile
        }
    }

    init {
        binding.root.setOnClickListener {
            listener?.handleLayoutEvent(EventType.ContainerTap, adapterPosition, it)
        }
    }

    override fun bind(item: User?) {
        if (item == null) return

        binding.layoutHeaderFragmentProfileTextViewNickname.setTextAsync(item.nickname)
        binding.layoutHeaderFragmentProfileTextViewUsername.setTextAsync("@${item.username}")
        kairos.load(item.avatarUrl)
            .scale(480, 480)
            .default(R.drawable.ic_header_scientist)
            .into(binding.layoutHeaderFragmentProfileImageAvatar)
    }
}