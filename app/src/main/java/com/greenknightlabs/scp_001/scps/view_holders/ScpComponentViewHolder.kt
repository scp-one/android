package com.greenknightlabs.scp_001.scps.view_holders

import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.fragments.PageViewModel
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.ComponentScpBinding
import com.greenknightlabs.scp_001.scps.models.Scp

class ScpComponentViewHolder(
    private val binding: ComponentScpBinding
) : RecyclerView.ViewHolder(binding.root) {
    // interfaces

    // functions
    fun bind(position: Int, vm: PageViewModel<Scp>, kairos: Kairos) {
        val scp = vm.items.value!![position]

        binding.scp = scp
//        binding.onTapAction =

        scp.media?.let { scpMedia ->
            kairos.load(scpMedia.url)
                .scale(360, 360)
                .default(R.drawable.ic_face)
                .into(binding.componentScpImageView)

            binding.componentScpImageView.doOnLayout { view ->
                view.layoutParams.height = scpMedia.calculateHeight(view.measuredWidth)
            }
        }
    }
}