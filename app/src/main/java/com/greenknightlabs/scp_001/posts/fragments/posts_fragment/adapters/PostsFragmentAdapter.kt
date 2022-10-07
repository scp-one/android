package com.greenknightlabs.scp_001.posts.fragments.posts_fragment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.ComponentMediaBinding
import com.greenknightlabs.scp_001.databinding.ComponentPostBinding
import com.greenknightlabs.scp_001.media.fragments.media_collection_fragment.adapters.MediaCollectionFragmentAdapter
import com.greenknightlabs.scp_001.posts.fragments.posts_fragment.PostsFragmentViewModel
import timber.log.Timber

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
            binding.onTapAction = adapter.vm::handleOnTapPost
            binding.postAuthorComponentListener = adapter.vm

            post.user.user?.avatarUrl?.let {
                kairos.load(it)
                    .scale(140, 140)
                    .default(R.drawable.default_avatar)
                    .into(binding.componentPostComponentAuthor.componentPostAuthorImageView)
            }

            post.media?.let {
                kairos.load(it.url)
                    .scale(360, 360)
                    .default(R.drawable.ic_face)
                    .into(binding.componentPostImageView)
            }

            binding.componentPostImageView.doOnLayout {
                it.layoutParams.height = post.media?.calculateHeight(it.measuredWidth) ?: 0
            }
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