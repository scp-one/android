package com.greenknightlabs.scp_001.posts.view_holders

import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.fragments.PageViewModel
import com.greenknightlabs.scp_001.app.util.Kairos
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
    fun bind(position: Int, vm: PageViewModel<Post>, listener: Listener, kairos: Kairos) {
        val post = vm.items.value!![position]

        binding.post = post
        binding.onTapAction = listener::handleOnTapPost
        binding.postAuthorComponentListener = listener

        post.user.user?.avatarUrl?.let {
            kairos.load(it)
                .scale(140, 140)
                .default(R.drawable.default_avatar)
                .into(binding.componentPostComponentAuthor.componentPostAuthorImageView)
        }

        post.media?.let { postMedia ->
            kairos.load(postMedia.url)
                .scale(360, 360)
                .default(R.drawable.ic_face)
                .into(binding.componentPostImageView)

            binding.componentPostImageView.doOnLayout { view ->
                view.layoutParams.height = postMedia.calculateHeight(view.measuredWidth)
            }
        }
    }
}