package com.greenknightlabs.scp_001.scps.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ScpStatus(val rawValue: String) {
    @SerialName("Draft")
    DRAFT("Draft"),
    @SerialName("Pending")
    PENDING("Pending"),
    @SerialName("Approved")
    APPROVED("Approved"),
    @SerialName("Rejected")
    REJECTED("Rejected")
}