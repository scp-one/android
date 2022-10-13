package com.greenknightlabs.scp_001.scps.view_holders

import android.view.View
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.util.Kairos
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

    // properties
    private var viewWidth: Int? = null

    // functions
    fun bind(position: Int, vm: ScpsViewModel, kairos: Kairos, preferences: Preferences) {
        val scp = vm.items.value!![position]

        binding.scp = scp
        binding.listener = vm

        if (preferences.loadScpImages.value == ScpLoadImages.IN_VIEW) {
            binding.componentScpImageView.visibility = View.GONE
        } else {
            binding.componentScpImageView.visibility = if (scp.media != null) View.VISIBLE else View.GONE

            scp.media?.let { scpMedia ->
                if (viewWidth == null) {
                    binding.componentScpImageView.doOnLayout { view ->
                        viewWidth = view.measuredWidth
                        binding.componentScpImageView.layoutParams.height = scpMedia.calculateHeight(viewWidth!!)
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
}
