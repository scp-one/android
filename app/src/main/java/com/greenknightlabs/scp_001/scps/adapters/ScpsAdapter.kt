package com.greenknightlabs.scp_001.scps.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.app.util.Preferences
import com.greenknightlabs.scp_001.databinding.ComponentScpBinding
import com.greenknightlabs.scp_001.scps.view_holders.ScpComponentViewHolder
import com.greenknightlabs.scp_001.scps.view_models.ScpsViewModel

class ScpsAdapter(
    private val vm: ScpsViewModel,
    private val kairos: Kairos,
    private val preferences: Preferences
) : RecyclerView.Adapter<ScpComponentViewHolder>() {
    // properties
    private lateinit var binding: ComponentScpBinding
    var screenWidth = 0

    // functions
    override fun getItemCount(): Int {
        return vm.items.value?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScpComponentViewHolder {
        binding = ComponentScpBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScpComponentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScpComponentViewHolder, position: Int) {
        holder.bind(position, vm, screenWidth, kairos, preferences)
    }
}
