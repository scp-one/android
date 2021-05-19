package com.mirenzen.scp_001.users.dtos

import com.mirenzen.scp_001.users.enums.UserAccessLevel

data class GetUsersFilterDto(
    val id: String? = null,
    val name: String? = null,
    val accessLevel: UserAccessLevel? = null,
    val sort: String? = null,
    val page: Int? = null
)