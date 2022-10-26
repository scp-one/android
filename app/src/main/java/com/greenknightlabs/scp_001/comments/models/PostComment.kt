package com.greenknightlabs.scp_001.comments.models

import com.greenknightlabs.scp_001.posts.models.PostRef
import com.greenknightlabs.scp_001.users.models.UserRef
import kotlinx.serialization.Serializable

@Serializable
data class PostComment(
    val id: String,
    val user: UserRef,
    val post: PostRef,
    val username: String,
    val nickname: String,
    val content: String,
    val reportsCount: Int,
    val createdAt: String,
)
