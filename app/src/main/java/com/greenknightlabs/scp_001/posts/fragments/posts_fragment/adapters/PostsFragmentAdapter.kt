package com.greenknightlabs.scp_001.posts.fragments.posts_fragment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.ComponentMediaBinding
import com.greenknightlabs.scp_001.databinding.ComponentPostBinding
import com.greenknightlabs.scp_001.media.fragments.media_collection_fragment.adapters.MediaCollectionFragmentAdapter
import com.greenknightlabs.scp_001.posts.fragments.posts_fragment.PostsFragmentViewModel

class PostsFragmentAdapter(
    private val vm: PostsFragmentViewModel,
    private val kairos: Kairos,
) : RecyclerView.Adapter<PostsFragmentAdapter.PostComponentViewHolder>() {
    // view holder
    class PostComponentViewHolder(
        private val binding: ComponentPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        // functions
        fun bind(position: Int, adapter: PostsFragmentAdapter, kairos: Kairos) {
            val post = adapter.vm.items.value!![position]

            binding.post = post
//            binding.componentMediaImageView.setOnClickListener {
//                adapter.vm.handleOnTapMedia(position)
//            }

//            kairos.load(media.url).scale(240, 240).default(R.drawable.ic_face).into(binding.componentMediaImageView)
        }
    }

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
        holder.bind(position, this, kairos)
    }
}