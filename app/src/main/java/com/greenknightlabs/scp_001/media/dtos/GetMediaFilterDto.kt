package com.greenknightlabs.scp_001.media.dtos

import com.greenknightlabs.scp_001.media.models.Media
import kotlinx.serialization.Serializable

@Serializable
data class GetMediaFilterDto(
    val id: String?,
    val uid: String?,
    val sort: String?,
    val cursor: String?,
    val limit: Int?,
)

fun GetMediaFilterDto.asMap(): Map<String, Any?> = GetMediaFilterDto::class.members.associate { it.name to it.toString() }