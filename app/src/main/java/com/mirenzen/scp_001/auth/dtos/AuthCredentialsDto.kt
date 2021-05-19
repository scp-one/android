package com.mirenzen.scp_001.auth.dtos

import kotlinx.serialization.Serializable

@Serializable
data class AuthCredentialsDto(
    val username: String? = null,
    val email: String? = null,
    val password: String
)