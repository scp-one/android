package com.greenknightlabs.scp_001.scps.enums

import kotlinx.serialization.Serializable

@Serializable
enum class ScpLength(val rawValue: String) {
    SHORT("Short"),
    MEDIUM("Medium"),
    LONG("Long"),
    VERY_LONG("Very Long")
}