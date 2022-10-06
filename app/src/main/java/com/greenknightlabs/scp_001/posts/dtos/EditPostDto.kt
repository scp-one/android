package com.greenknightlabs.scp_001.posts.dtos

import com.greenknightlabs.scp_001.posts.enums.PostStatus
import com.greenknightlabs.scp_001.posts.enums.PostVisibility
import kotlinx.serialization.Serializable

@Serializable
data class EditPostDto(
    val title: String? = null,
    val content: String? = null,
    val mediaId: String? = null,
    val visibility: PostVisibility? = null,
    val status: PostStatus? = null,
    val sourceUrl: String? = null,
    val nullify: List<String>? = null
)