package com.greenknightlabs.scp_001.scps.fragments.scps_fragment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.ComponentScpBinding
import com.greenknightlabs.scp_001.scps.fragments.scps_fragment.ScpsFragmentViewModel
import com.greenknightlabs.scp_001.scps.view_holders.ScpComponentViewHolder

class ScpsFragmentAdapter(
    private val vm: ScpsFragmentViewModel,
    private val kairos: Kairos,
) : RecyclerView.Adapter<ScpComponentViewHolder>() {
    // properties
    private lateinit var binding: ComponentScpBinding

    // functions
    override fun getItemCount(): Int {
        return vm.items.value?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScpComponentViewHolder {
        binding = ComponentScpBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScpComponentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScpComponentViewHolder, position: Int) {
        holder.bind(position, vm, kairos)
    }
}