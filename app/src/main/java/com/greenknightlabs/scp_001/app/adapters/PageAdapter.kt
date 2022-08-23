package com.greenknightlabs.scp_001.app.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.app.interfaces.BindableView
import kotlinx.coroutines.runBlocking

abstract class PageAdapter<T>(
    private val items: List<T>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    open val headerOffset = 0

    override fun getItemCount(): Int {
        return items.size + headerOffset
    }

    override fun getItemViewType(position: Int): Int {
        return getItemLayoutId(position)
    }

    protected abstract fun getItemLayoutId(position: Int): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return getViewHolder(parent, viewType)
    }

    abstract fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        runBlocking {
            (holder as? BindableView<T>)?.bind(items[position])
        }
    }
}