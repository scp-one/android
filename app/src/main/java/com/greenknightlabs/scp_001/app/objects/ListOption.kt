package com.greenknightlabs.scp_001.app.objects

data class ListOption(
    val name: String,
    val iconId: Int?,
    val onTapAction: () -> Unit
)