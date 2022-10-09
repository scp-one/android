package com.greenknightlabs.scp_001.scps.models

import com.greenknightlabs.scp_001.scps.enums.ScpClassType
import com.greenknightlabs.scp_001.scps.enums.ScpLength
import com.greenknightlabs.scp_001.scps.enums.ScpStatus
import com.greenknightlabs.scp_001.scps.enums.ScpVisibility
import com.greenknightlabs.scp_001.users.models.UserRef
import kotlinx.serialization.Serializable

@Serializable
data class Scp(
    val id: String,
    val user: UserRef,
    val name: String,
    val number: Int,
    val series: Int,
    val classType: ScpClassType,
    val contentBlocks: List<ScpContentBlock>,
    val media: ScpMedia? = null,
    val sourceUrl: String? = null,
    val license: String? = null,
    val length: ScpLength,
    val visibility: ScpVisibility,
    val status: ScpStatus,
    val updatedAt: String,
    val createdAt: String,

    var saved: Boolean,
    var liked: Boolean,
    var read: Boolean,
)