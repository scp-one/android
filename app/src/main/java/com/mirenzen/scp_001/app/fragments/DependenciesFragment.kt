package com.mirenzen.scp_001.app.fragments

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mirenzen.scp_001.app.adapters.PageAdapter
import com.mirenzen.scp_001.app.objects.Dependency
import com.mirenzen.scp_001.app.view_models.EmptyViewModel

class DependenciesFragment : PageFragment<Dependency, EmptyViewModel<Dependency>>() {
    private val dependencies = listOf(
        Dependency("test", "https://test.com")
    )

    override val viewModel: EmptyViewModel<Dependency>
        get() = TODO("Not yet implemented")

    override fun adapterForView(): PageAdapter<Dependency> {
        return object : PageAdapter<Dependency>(dependencies) {
            override fun getItemLayoutId(position: Int): Int {
                TODO("Not yet implemented")
            }

            override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                TODO("Not yet implemented")
            }
        }
    }

    override fun paginate(refresh: Boolean) {
        return
    }
}