package com.greenknightlabs.scp_001.users.dtos

import com.greenknightlabs.scp_001.users.enums.UserAccessLevel
import kotlinx.serialization.Serializable

@Serializable
data class EditUserDto(
    val nickname: String? = null,
    val avatarUrl: String? = null,
    val accessLevel: UserAccessLevel? = null,
    val nullify: List<String>? = null
)