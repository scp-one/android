package com.greenknightlabs.scp_001.scps.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ScpClassType(val rawValue: String) {
    @SerialName("Safe")
    SAFE("Safe"),
    @SerialName("Euclid")
    EUCLID("Euclid"),
    @SerialName("Keter")
    KETER("Keter"),
    @SerialName("Thaumiel")
    THAUMIEL("Thaumiel"),
    @SerialName("Apollyon")
    APOLLYON("Apollyon"),
    @SerialName("Archon")
    ARCHON("Archon"),
    @SerialName("Neutralized")
    NEUTRALIZED("Neutralized"),
    @SerialName("Explained")
    EXPLAINED("Explained"),
    @SerialName("Esoteric")
    ESOTERIC("Esoteric"),
    @SerialName("Unknown")
    UNKNOWN("Unknown"),
}