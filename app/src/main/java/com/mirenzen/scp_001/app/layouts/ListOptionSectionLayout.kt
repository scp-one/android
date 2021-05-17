package com.mirenzen.scp_001.app.layouts

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mirenzen.scp_001.R
import com.mirenzen.scp_001.app.extensions.setTextAsync
import com.mirenzen.scp_001.app.interfaces.BindableView
import com.mirenzen.scp_001.app.objects.ListOption
import com.mirenzen.scp_001.app.objects.ListOptionSection
import com.mirenzen.scp_001.databinding.LayoutListOptionSectionBinding

class ListOptionSectionLayout(
    val binding: LayoutListOptionSectionBinding,
    private val listener: Listener?
) : RecyclerView.ViewHolder(binding.root), BindableView<ListOptionSection> {
    enum class EventType {
        ContainerTap
    }

    interface Listener {
        fun handleLayoutEvent(event: EventType, sectionIndex: Int, optionIndex: Int)
    }

    companion object {
        fun layoutId(): Int {
            return R.layout.layout_list_option_section
        }
    }

    init {
        binding.option0.layoutListOptionTextView.setOnClickListener {
            listener?.handleLayoutEvent(EventType.ContainerTap, adapterPosition, 0)
        }
        binding.option1.layoutListOptionTextView.setOnClickListener {
            listener?.handleLayoutEvent(EventType.ContainerTap, adapterPosition, 1)
        }
        binding.option2.layoutListOptionTextView.setOnClickListener {
            listener?.handleLayoutEvent(EventType.ContainerTap, adapterPosition, 2)
        }
        binding.option3.layoutListOptionTextView.setOnClickListener {
            listener?.handleLayoutEvent(EventType.ContainerTap, adapterPosition, 3)
        }
    }

    override suspend fun bind(item: ListOptionSection) {
        binding.listOptionSectionTitle.setTextAsync(item.title)

        val drawableIdEnd = R.drawable.ic_chevron_right
        val option0 = item.options.elementAtOrNull(0)
        val option1 = item.options.elementAtOrNull(1)
        val option2 = item.options.elementAtOrNull(2)
        val option3 = item.options.elementAtOrNull(3)

        if (option0 != null) {
            binding.option0.layoutListOptionTextView.setTextAsync(option0.name)
            binding.option0.layoutListOptionTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                option0.iconId ?: 0, 0, drawableIdEnd, 0
            )
        }

        if (option1 != null) {
            binding.option1.layoutListOptionTextView.setTextAsync(option1.name)
            binding.option1.layoutListOptionTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                option1.iconId ?: 0, 0, drawableIdEnd, 0
            )
        }

        if (option2 != null) {
            binding.option2.layoutListOptionTextView.setTextAsync(option2.name)
            binding.option2.layoutListOptionTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                option2.iconId ?: 0, 0, drawableIdEnd, 0
            )
        }

        if (option3 != null) {
            binding.option3.layoutListOptionTextView.setTextAsync(option3.name)
            binding.option3.layoutListOptionTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                option3.iconId ?: 0, 0, drawableIdEnd, 0
            )
        }

        binding.option0.layoutListOptionTextView.visibility = getVisibility(option0)
        binding.option1.layoutListOptionTextView.visibility = getVisibility(option1)
        binding.option2.layoutListOptionTextView.visibility = getVisibility(option2)
        binding.option3.layoutListOptionTextView.visibility = getVisibility(option3)
    }

    private fun getVisibility(option: ListOption?): Int {
        return when (option == null) {
            true -> View.GONE
            else -> View.VISIBLE
        }
    }
}