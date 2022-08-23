package com.greenknightlabs.scp_001.app.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.app.adapters.PageAdapter
import com.greenknightlabs.scp_001.app.extensions.pushWebView
import com.greenknightlabs.scp_001.app.layouts.DependencyLayout
import com.greenknightlabs.scp_001.app.objects.Dependency
import com.greenknightlabs.scp_001.app.view_models.EmptyViewModel
import com.greenknightlabs.scp_001.databinding.LayoutDependencyBinding

class DependenciesFragment : PageFragment<Dependency, EmptyViewModel<Dependency>>(),
    DependencyLayout.Listener
{
    private val dependencies = listOf(
        Dependency(
            "Flaticon",
            "https://www.flaticon.com/authors/freepik"
        )
    )

    override val viewModel: EmptyViewModel<Dependency> by viewModels()

    override fun activityTitle(): String {
        return "Licenses"
    }

    override fun adapterForView(): PageAdapter<Dependency> {
        return object : PageAdapter<Dependency>(dependencies) {
            override fun getItemLayoutId(position: Int): Int {
                return DependencyLayout.layoutId()
            }

            override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val binding = LayoutDependencyBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return DependencyLayout(binding, this@DependenciesFragment)
            }
        }
    }

    override fun paginate(refresh: Boolean) {
        return
    }

    override fun handleLayoutEvent(event: DependencyLayout.EventType, index: Int) {
        dependencies.getOrNull(index)?.let {
            activity?.pushWebView(it.url)
        }
    }
}