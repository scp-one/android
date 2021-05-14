package com.mirenzen.scp_001.auth.dtos

data class AuthCredentialsDto(
    val username: String?,
    val email: String?,
    val password: String
)