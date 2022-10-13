package com.greenknightlabs.scp_001.posts.fragments.posts_fragment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.fragments.PageViewModel
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.ComponentMediaBinding
import com.greenknightlabs.scp_001.databinding.ComponentPostBinding
import com.greenknightlabs.scp_001.media.fragments.media_collection_fragment.adapters.MediaCollectionFragmentAdapter
import com.greenknightlabs.scp_001.posts.fragments.posts_fragment.PostsFragmentViewModel
import com.greenknightlabs.scp_001.posts.interfaces.PostAuthorComponentListener
import com.greenknightlabs.scp_001.posts.models.Post
import com.greenknightlabs.scp_001.posts.view_holders.PostComponentViewHolder
import timber.log.Timber

class PostsFragmentAdapter(
    private val vm: PostsFragmentViewModel,
    private val kairos: Kairos,
) : RecyclerView.Adapter<PostComponentViewHolder>() {
    // properties
    private lateinit var binding: ComponentPostBinding

    // functions
    override fun getItemCount(): Int {
        return vm.items.value?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostComponentViewHolder {
        binding = ComponentPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostComponentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostComponentViewHolder, position: Int) {
        holder.bind(position, vm, vm, kairos)
    }
}