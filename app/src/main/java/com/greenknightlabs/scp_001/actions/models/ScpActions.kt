package com.greenknightlabs.scp_001.actions.models

import com.greenknightlabs.scp_001.scps.models.ScpRef
import com.greenknightlabs.scp_001.users.models.UserRef
import kotlinx.serialization.Serializable

@Serializable
data class ScpActions(
    val id: String,
    val user: UserRef,
    val scp: ScpRef,
    var saved: Boolean,
    var liked: Boolean,
    var read: Boolean,
    var savedAt: String? = null,
    var likedAt: String? = null,
    var readAt: String? = null
)