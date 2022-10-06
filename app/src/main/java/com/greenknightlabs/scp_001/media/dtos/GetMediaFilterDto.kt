package com.greenknightlabs.scp_001.media.dtos

import kotlinx.serialization.Serializable

@Serializable
data class GetMediaFilterDto(
    val id: String?,
    val uid: String?,
    val sort: String?,
    val cursor: String?,
    val limit: Int?,
)