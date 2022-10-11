package com.greenknightlabs.scp_001.scps.view_holders

import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.fragments.PageViewModel
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.ComponentScpBinding
import com.greenknightlabs.scp_001.posts.interfaces.PostAuthorComponentListener
import com.greenknightlabs.scp_001.posts.models.Post
import com.greenknightlabs.scp_001.scps.models.Scp
import com.greenknightlabs.scp_001.scps.view_models.ScpsViewModel

class ScpComponentViewHolder(
    private val binding: ComponentScpBinding
) : RecyclerView.ViewHolder(binding.root) {
    // interfaces
    interface Listener {
        fun handleOnTapScp(scp: Scp)
        fun handleOnTapRead(scp: Scp)
        fun handleOnTapLike(scp: Scp)
        fun handleOnTapSave(scp: Scp)
    }

    // properties
    private var viewWidth: Int? = null

    // functions
    fun bind(position: Int, vm: ScpsViewModel, kairos: Kairos) {
        val scp = vm.items.value!![position]

        binding.scp = scp
        binding.listener = vm

        scp.media?.let { scpMedia ->
            if (viewWidth == null) {
                binding.componentScpImageView.doOnLayout { view ->
                    viewWidth = view.measuredWidth
                    view.layoutParams.height = scpMedia.calculateHeight(viewWidth!!)
                }
            } else {
                binding.componentScpImageView.layoutParams.height = scpMedia.calculateHeight(viewWidth!!)
            }

            kairos.load(scpMedia.url)
                .scale(360, 360)
                .default(R.drawable.ic_face)
                .into(binding.componentScpImageView)
        }
    }
}
