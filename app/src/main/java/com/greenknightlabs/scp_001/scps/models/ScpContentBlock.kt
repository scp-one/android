package com.greenknightlabs.scp_001.scps.models

import kotlinx.serialization.Serializable

@Serializable
data class ScpContentBlock(
    val collapsible: Boolean,
    val title: String? = null,
    val mdContent: String? = null,
    val table: ScpTable? = null,
    val audioUrl: String? = null,
    val contentBlocks: List<ScpContentBlock>? = null
)