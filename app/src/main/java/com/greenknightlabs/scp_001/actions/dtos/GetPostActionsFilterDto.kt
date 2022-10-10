package com.greenknightlabs.scp_001.actions.dtos

import com.greenknightlabs.scp_001.actions.enums.PostActionsType
import kotlinx.serialization.Serializable

@Serializable
data class GetPostActionsFilterDto(
    val id: String? = null,
    val uid: String? = null,
    val pid: String? = null,
    val type: PostActionsType,
    val sort: String? = null,
    val cursor: String? = null,
    val limit: Int? = null
)