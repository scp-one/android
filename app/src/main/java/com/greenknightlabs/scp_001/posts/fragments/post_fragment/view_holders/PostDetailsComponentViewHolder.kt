package com.greenknightlabs.scp_001.posts.fragments.post_fragment.view_holders

import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.databinding.ComponentPostDetailsBinding
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.PostFragmentViewModel
import com.greenknightlabs.scp_001.posts.interfaces.PostAuthorComponentListener
import com.greenknightlabs.scp_001.posts.models.Post

class PostDetailsComponentViewHolder(
    private val binding: ComponentPostDetailsBinding
) : RecyclerView.ViewHolder(binding.root) {
    // interfaces
    interface Listener : PostAuthorComponentListener {
        fun handleOnTapPostSource(url: String)
        fun handleOnTapAddComment()
    }

    // functions
    fun bind(vm: PostFragmentViewModel, screenWidth: Int) {
        binding.post = vm.post.value!!
        binding.listener = vm

        binding.componentPostDetailsComponentPostAuthor.componentPostAuthorImageView.load(when (vm.post.value?.user?.user?.avatarUrl) {
            null -> R.drawable.default_avatar
            else -> vm.post.value?.user?.user?.avatarUrl!!
        }) {
            size(140)
            transformations(CircleCropTransformation())
            crossfade(true)
            error(R.drawable.default_avatar)
        }

        vm.post.value?.media?.let { postMedia ->
            binding.componentPostDetailsImageView.layoutParams.height = postMedia.calculateHeight(screenWidth)
            binding.componentPostDetailsImageView.load(postMedia.url) {
                size(360)
                crossfade(true)
                error(R.drawable.ic_cancel)
            }
        }
    }
}
