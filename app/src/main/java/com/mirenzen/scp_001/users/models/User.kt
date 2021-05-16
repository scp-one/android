package com.mirenzen.scp_001.users.models

import com.mirenzen.scp_001.users.enums.UserAccessLevel
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val nickname: String,
    val avatarUrl: String?,
    val accessLevel: UserAccessLevel,
    val updatedAt: String,
    val createdAt: String
)