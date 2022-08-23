package com.greenknightlabs.scp_001.scps.models

import com.greenknightlabs.scp_001.scps.enums.ScpClassType
import com.greenknightlabs.scp_001.scps.enums.ScpStatus
import com.greenknightlabs.scp_001.scps.enums.ScpVisibility
import kotlinx.serialization.Serializable

@Serializable
data class Scp(
    val id: String,
    val uid: String,
    val user: String? = null,
    val name: String,
    val number: Int,
    val classType: ScpClassType,
    val procedures: String? = null,
    val description: String? = null,
    val addendums: String? = null,
    val visibility: ScpVisibility,
    val status: ScpStatus,
    val updatedAt: String,
    val createdAt: String
)