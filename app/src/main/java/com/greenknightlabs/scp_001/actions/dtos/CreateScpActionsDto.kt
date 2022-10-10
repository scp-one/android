package com.greenknightlabs.scp_001.actions.dtos

import com.greenknightlabs.scp_001.actions.enums.ScpActionsType
import kotlinx.serialization.Serializable

@Serializable
data class CreateScpActionsDto(
    val type: ScpActionsType,
    val state: Boolean
)