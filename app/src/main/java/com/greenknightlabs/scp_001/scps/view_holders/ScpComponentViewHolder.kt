package com.greenknightlabs.scp_001.scps.view_holders

import android.view.View
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.util.Preferences
import com.greenknightlabs.scp_001.databinding.ComponentScpBinding
import com.greenknightlabs.scp_001.scps.enums.ScpLoadImages
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

    // functions
    fun bind(position: Int, vm: ScpsViewModel, screenWidth: Int, preferences: Preferences) {
        val scp = vm.items.value!![position]

        binding.scp = scp
        binding.listener = vm

        if (preferences.loadScpImages.value == ScpLoadImages.IN_VIEW) {
            binding.componentScpImageView.visibility = View.GONE
        } else {
            binding.componentScpImageView.visibility = if (scp.media != null) View.VISIBLE else View.GONE

            scp.media?.let { scpMedia ->
                binding.componentScpImageView.layoutParams.height = scpMedia.calculateHeight(screenWidth)

                binding.componentScpImageView.load(scpMedia.url) {
                    size(360)
                    crossfade(true)
                    error(R.drawable.ic_cancel)
                }
            }
        }
    }
}
