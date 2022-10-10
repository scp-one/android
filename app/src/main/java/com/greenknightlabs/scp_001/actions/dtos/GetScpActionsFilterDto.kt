package com.greenknightlabs.scp_001.actions.dtos

import com.greenknightlabs.scp_001.actions.enums.ScpActionsType
import kotlinx.serialization.Serializable

@Serializable
data class GetScpActionsFilterDto(
    val id: String? = null,
    val uid: String? = null,
    val scpid: String? = null,
    val type: ScpActionsType,
    val sort: String? = null,
    val cursor: String? = null,
    val limit: Int? = null
)
