package com.greenknightlabs.scp_001.scps.models

import kotlinx.serialization.Serializable

@Serializable
data class ScpTable(
    val headers: List<String>? = null,
    val rows: List<List<String>>
)