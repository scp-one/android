package com.mirenzen.scp_001.users.dtos

import com.mirenzen.scp_001.users.enums.UserAccessLevel

data class GetUsersFilterDto(
    val id: String?,
    val name: String?,
    val accessLevel: UserAccessLevel?,
    val sort: String?,
    val page: Int?
)