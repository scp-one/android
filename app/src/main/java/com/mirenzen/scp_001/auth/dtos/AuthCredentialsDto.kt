package com.mirenzen.scp_001.auth.dtos

import kotlinx.serialization.Serializable

@Serializable
data class AuthCredentialsDto(
    val username: String?,
    val email: String?,
    val password: String
)