package com.mirenzen.scp_001.app.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mirenzen.scp_001.app.interfaces.BindableView

abstract class PageAdapter<T>(
    private val items: List<T>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
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
        (holder as? BindableView<T>)?.bind(items[position])
    }
}