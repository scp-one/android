package com.greenknightlabs.scp_001.comments.dtos

import kotlinx.serialization.Serializable

@Serializable
data class GetPostCommentsFilterDto(
    val id: String? = null,
    val uid: String? = null,
    val pid: String? = null,
    val sort: String? = null,
    val cursor: String? = null,
    val limit: Int? = null
)
