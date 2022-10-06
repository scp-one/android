package com.greenknightlabs.scp_001.scps.enums

import kotlinx.serialization.Serializable

@Serializable
enum class ScpStatus(val rawValue: String) {
    DRAFT("Draft"),
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected")
}