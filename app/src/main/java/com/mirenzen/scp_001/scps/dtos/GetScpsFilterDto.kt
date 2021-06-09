package com.mirenzen.scp_001.scps.dtos

import com.mirenzen.scp_001.scps.enums.ScpClassType
import com.mirenzen.scp_001.scps.enums.ScpStatus
import com.mirenzen.scp_001.scps.enums.ScpVisibility
import kotlinx.serialization.Serializable

@Serializable
data class GetScpsFilterDto(
    val id: String? = null,
    val uid: String? = null,
    val name: String? = null,
    val number: Int? = null,
    val classType: ScpClassType? = null,
    val visibility: ScpVisibility? = null,
    val status: ScpStatus? = null,
    val sort: String? = null,
    val cursor: String? = null
)