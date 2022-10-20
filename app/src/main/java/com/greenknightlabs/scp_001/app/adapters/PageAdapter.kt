package com.greenknightlabs.scp_001.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.app.fragments.PageViewModel
import com.greenknightlabs.scp_001.databinding.ComponentFailToLoadPageBinding

class PageAdapter<T>(
    private val vm: PageViewModel<T>,
) : RecyclerView.Adapter<FailToLoadPageComponentViewHolder<T>>() {
    // properties
    private lateinit var binding: ComponentFailToLoadPageBinding

    // functions
    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FailToLoadPageComponentViewHolder<T> {
        binding = ComponentFailToLoadPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FailToLoadPageComponentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FailToLoadPageComponentViewHolder<T>, position: Int) {
        holder.bind(vm)
    }
}

class FailToLoadPageComponentViewHolder<T>(
    private val binding: ComponentFailToLoadPageBinding
) : RecyclerView.ViewHolder(binding.root) {
    // functions
    fun bind(vm: PageViewModel<T>) {
        binding.vm = vm
    }
}
