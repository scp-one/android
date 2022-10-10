package com.greenknightlabs.scp_001.actions.models

import com.greenknightlabs.scp_001.posts.models.PostRef
import com.greenknightlabs.scp_001.users.models.UserRef
import kotlinx.serialization.Serializable

@Serializable
data class PostActions(
    val id: String,
    val user: UserRef,
    val post: PostRef,
    var liked: Boolean,
    var likedAt: String? = null
)