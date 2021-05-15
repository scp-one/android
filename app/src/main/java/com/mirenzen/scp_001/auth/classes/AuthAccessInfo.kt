package com.mirenzen.scp_001.auth.classes

import kotlinx.serialization.Serializable

@Serializable
data class AuthAccessInfo(
    val accessToken: String,
    val refreshToken: String?
)