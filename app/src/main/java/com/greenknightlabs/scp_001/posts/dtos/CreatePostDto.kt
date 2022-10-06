package com.greenknightlabs.scp_001.posts.dtos

import com.greenknightlabs.scp_001.posts.enums.PostStatus
import kotlinx.serialization.Serializable

@Serializable
data class CreatePostDto(
    val title: String,
    val content: String,
    val mediaId: String? = null,
    val status: PostStatus,
    val sourceUrl: String? = null
)