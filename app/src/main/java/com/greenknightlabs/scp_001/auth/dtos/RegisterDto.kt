package com.greenknightlabs.scp_001.auth.dtos

import kotlinx.serialization.Serializable

@Serializable
data class RegisterDto(
    val username: String,
    val email: String,
    val password: String
)