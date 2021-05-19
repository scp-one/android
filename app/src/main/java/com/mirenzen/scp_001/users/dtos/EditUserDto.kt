package com.mirenzen.scp_001.users.dtos

import kotlinx.serialization.Serializable

@Serializable
data class EditUserDto(
    val nickname: String? = null,
    val avatarUrl: String? = null,
    val nullify: List<String>? = null
)