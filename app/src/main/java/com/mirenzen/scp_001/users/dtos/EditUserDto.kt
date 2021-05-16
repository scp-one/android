package com.mirenzen.scp_001.users.dtos

import kotlinx.serialization.Serializable

@Serializable
data class EditUserDto(
    val nickname: String?,
    val avatarUrl: String?,
    val nullify: List<String>?
)