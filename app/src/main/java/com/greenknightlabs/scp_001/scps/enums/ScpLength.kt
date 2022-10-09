package com.greenknightlabs.scp_001.scps.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ScpLength(val rawValue: String) {
    @SerialName("Short")
    SHORT("Short"),
    @SerialName("Medium")
    MEDIUM("Medium"),
    @SerialName("Long")
    LONG("Long"),
    @SerialName("Very Long")
    VERY_LONG("Very Long")
}