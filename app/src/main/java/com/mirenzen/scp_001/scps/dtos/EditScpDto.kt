package com.mirenzen.scp_001.scps.dtos

import com.mirenzen.scp_001.scps.enums.ScpClassType
import com.mirenzen.scp_001.scps.enums.ScpStatus
import com.mirenzen.scp_001.scps.enums.ScpVisibility
import com.mirenzen.scp_001.scps.models.Addendum
import kotlinx.serialization.Serializable

@Serializable
data class EditScpDto(
    val name: String? = null,
    val number: Int? = null,
    val classType: ScpClassType? = null,
    val procedures: String? = null,
    val description: String? = null,
    val addendums: List<Addendum>? = null,
    val visibility: ScpVisibility? = null,
    val status: ScpStatus? = null,
    val nullify: List<String>? = null
)