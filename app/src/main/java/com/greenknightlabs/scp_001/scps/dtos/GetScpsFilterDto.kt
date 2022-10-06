package com.greenknightlabs.scp_001.scps.dtos

import com.greenknightlabs.scp_001.scps.enums.ScpClassType
import com.greenknightlabs.scp_001.scps.enums.ScpStatus
import com.greenknightlabs.scp_001.scps.enums.ScpVisibility
import kotlinx.serialization.Serializable

@Serializable
data class GetScpsFilterDto(
    val id: String? = null,
    val uid: String? = null,
    val name: String? = null,
    val number: Int? = null,
    val series: Int? = null,
    val classType: ScpClassType? = null,
    val visibility: ScpVisibility? = null,
    val status: ScpStatus? = null,
    val sort: String? = null,
    val cursor: String? = null,
    val limit: Int? = null,
)