package com.greenknightlabs.scp_001.comments.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CreatePostCommentDto(
    val content: String
)
