package com.greenknightlabs.scp_001.media.models

import com.greenknightlabs.scp_001.users.models.UserRef
import kotlinx.serialization.Serializable

@Serializable
data class Media(
    val id: String,
    val user: UserRef,
    val url: String,
    val width: Int,
    val height: Int,
    val mimeType: String,
    val createdAt: String,
)
