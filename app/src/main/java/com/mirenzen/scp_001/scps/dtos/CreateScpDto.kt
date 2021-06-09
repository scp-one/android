package com.mirenzen.scp_001.scps.dtos

import com.mirenzen.scp_001.scps.enums.ScpClassType
import com.mirenzen.scp_001.scps.models.Addendum
import kotlinx.serialization.Serializable

@Serializable
data class CreateScpDto(
    val name: String,
    val number: Int,
    val classType: ScpClassType,
    val procedures: String? = null,
    val description: String? = null,
    val addendums: List<Addendum>? = null
)