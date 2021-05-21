package com.mirenzen.scp_001.app.layouts

import androidx.recyclerview.widget.RecyclerView
import com.mirenzen.scp_001.R
import com.mirenzen.scp_001.app.extensions.setTextAsync
import com.mirenzen.scp_001.app.interfaces.BindableView
import com.mirenzen.scp_001.app.objects.Dependency
import com.mirenzen.scp_001.databinding.LayoutDependencyBinding

class DependencyLayout(
    val binding: LayoutDependencyBinding,
    private val listener: Listener?
) : RecyclerView.ViewHolder(binding.root), BindableView<Dependency> {
    enum class EventType {
        ContainerTap
    }

    interface Listener {
        fun handleLayoutEvent(event: EventType, index: Int)
    }

    companion object {
        fun layoutId(): Int {
            return R.layout.layout_dependency
        }
    }

    init {
        binding.layoutDependencyContainer.setOnClickListener {
            listener?.handleLayoutEvent(EventType.ContainerTap, adapterPosition)
        }
    }

    override fun bind(item: Dependency) {
        binding.layoutDependencyName.setTextAsync(item.name)
        binding.layoutDependencyUrl.setTextAsync(item.url)
    }
}