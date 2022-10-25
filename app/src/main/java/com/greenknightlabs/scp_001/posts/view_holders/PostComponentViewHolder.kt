package com.greenknightlabs.scp_001.posts.view_holders

import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.extensions.setTextAsync
import com.greenknightlabs.scp_001.app.view_models.PageViewModel
import com.greenknightlabs.scp_001.databinding.ComponentPostBinding
import com.greenknightlabs.scp_001.posts.interfaces.PostAuthorComponentListener
import com.greenknightlabs.scp_001.posts.models.Post

class PostComponentViewHolder(
    private val binding: ComponentPostBinding
) : RecyclerView.ViewHolder(binding.root) {
    // interfaces
    interface Listener : PostAuthorComponentListener {
        fun handleOnTapPost(post: Post)
    }

    // functions
    fun bind(position: Int, vm: PageViewModel<Post>, listener: Listener, screenWidth: Int) {
        val post = vm.items.value!![position]

        binding.post = post
        binding.listener = listener

        binding.componentPostTitleText.setTextAsync(post.title.take(128))
        binding.componentPostContentText.setTextAsync(post.content.take(150))

        binding.componentPostComponentAuthor.componentPostAuthorImageView.load(when (post.user.user?.avatarUrl) {
            null -> R.drawable.default_avatar
            else -> post.user.user?.avatarUrl!!
        }) {
            size(140)
            transformations(CircleCropTransformation())
            crossfade(true)
            error(R.drawable.default_avatar)
        }

        post.media?.let { postMedia ->
            binding.componentPostImageView.layoutParams.height = postMedia.calculateHeight(screenWidth)
            binding.componentPostImageView.load(postMedia.url) {
                size(360)
                crossfade(true)
                error(R.drawable.ic_cancel)
            }
        }
    }
}
