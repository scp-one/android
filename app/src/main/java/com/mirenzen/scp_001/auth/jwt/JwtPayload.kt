package com.mirenzen.scp_001.auth.jwt

data class JwtPayload (
    val id: String,
    val username: String,
    val iat: Long,
    val exp: Long
)