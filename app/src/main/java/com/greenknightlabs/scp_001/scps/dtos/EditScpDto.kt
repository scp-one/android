package com.greenknightlabs.scp_001.scps.dtos

import com.greenknightlabs.scp_001.scps.enums.ScpClassType
import com.greenknightlabs.scp_001.scps.enums.ScpStatus
import com.greenknightlabs.scp_001.scps.enums.ScpVisibility
import kotlinx.serialization.Serializable

@Serializable
data class EditScpDto(
    val name: String? = null,
    val number: Int? = null,
    val classType: ScpClassType? = null,
    val procedures: String? = null,
    val description: String? = null,
    val addendums: String? = null,
    val visibility: ScpVisibility? = null,
    val status: ScpStatus? = null,
    val nullify: List<String>? = null
)