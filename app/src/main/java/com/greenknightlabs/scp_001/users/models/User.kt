package com.greenknightlabs.scp_001.users.models

import com.greenknightlabs.scp_001.users.enums.UserAccessLevel
import com.greenknightlabs.scp_001.users.enums.UserEntitlements
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val nickname: String,
    val avatarUrl: String? = null,
    val accessLevel: UserAccessLevel,
    val entitlements: List<String>?,
    val mediaCount: Int,
    val postsCount: Int,
    val updatedAt: String,
    val createdAt: String
)