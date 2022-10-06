package com.greenknightlabs.scp_001.posts.dtos

import com.greenknightlabs.scp_001.posts.enums.PostStatus
import com.greenknightlabs.scp_001.posts.enums.PostVisibility
import kotlinx.serialization.Serializable

@Serializable
data class GetPostsFilterDto(
    val id: String? = null,
    val uid: String? = null,
    val visibility: PostVisibility? = null,
    val status: PostStatus? = null,
    val sort: String? = null,
    val cursor: String? = null,
    val limit: Int? = null
)