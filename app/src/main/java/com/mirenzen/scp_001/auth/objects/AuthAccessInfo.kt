package com.mirenzen.scp_001.auth.objects

import kotlinx.serialization.Serializable

@Serializable
data class AuthAccessInfo(
    val accessToken: String,
    val refreshToken: String?
)