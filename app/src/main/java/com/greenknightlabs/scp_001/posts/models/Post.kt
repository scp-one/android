package com.greenknightlabs.scp_001.posts.models

import com.greenknightlabs.scp_001.posts.enums.PostStatus
import com.greenknightlabs.scp_001.posts.enums.PostVisibility
import com.greenknightlabs.scp_001.users.models.UserRef
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: String,
    val user: UserRef,
    val title: String,
    val content: String,
    val media: PostMedia? = null,
    val visibility: PostVisibility,
    val status: PostStatus,
    val sourceUrl: String? = null,
    val likesCount: Int,
    val reportsCount: Int,
    val publishedAt: String? = null,
    val updatedAt: String,
    val createdAt: String,

    var liked: Boolean
)