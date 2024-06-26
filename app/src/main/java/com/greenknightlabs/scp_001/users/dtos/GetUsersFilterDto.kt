package com.greenknightlabs.scp_001.users.dtos

import com.greenknightlabs.scp_001.users.enums.UserAccessLevel
import kotlinx.serialization.Serializable

@Serializable
data class GetUsersFilterDto(
    val id: String? = null,
    val name: String? = null,
    val accessLevel: UserAccessLevel? = null,
    val sort: String? = null,
    val cursor: String? = null,
    val limit: Int? = null
)