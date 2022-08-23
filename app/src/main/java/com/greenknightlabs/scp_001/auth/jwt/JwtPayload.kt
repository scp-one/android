package com.greenknightlabs.scp_001.auth.jwt

import kotlinx.serialization.Serializable

@Serializable
data class JwtPayload (
    val id: String,
    val username: String,
    val iat: Long,
    val exp: Long
)