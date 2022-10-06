package com.greenknightlabs.scp_001.users.dtos

import kotlinx.serialization.Serializable

@Serializable
data class DeleteUserDto(
    val password: String
)