package com.greenknightlabs.scp_001.actions.dtos

import com.greenknightlabs.scp_001.actions.enums.PostActionsType
import kotlinx.serialization.Serializable

@Serializable
data class CreatePostActionsDto(
    val type: PostActionsType,
    val state: Boolean
)