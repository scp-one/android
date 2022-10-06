package com.greenknightlabs.scp_001.posts.models

import kotlinx.serialization.Serializable

@Serializable
data class PostMedia(
    val url: String,
    val width: Int,
    val height: Int,
    val caption: String? = null
)