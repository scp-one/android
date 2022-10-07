package com.greenknightlabs.scp_001.posts.interfaces

import android.view.View
import com.greenknightlabs.scp_001.posts.models.Post

interface PostAuthorComponentListener {
    fun handleOnTapPostAuthor(post: Post)
    fun handleOnTapPostAuthorMore(post: Post, view: View)
}