package com.greenknightlabs.scp_001.scps.enums

import kotlinx.serialization.Serializable

@Serializable
enum class ScpClassType(val rawValue: String) {
    SAFE("Safe"),
    EUCLID("Euclid"),
    KETER("Keter"),
    THAUMIEL("Thaumiel"),
    APOLLYON("Apollyon"),
    ARCHON("Archon"),
    NEUTRALIZED("Neutralized"),
    EXPLAINED("Explained"),
    ESOTERIC("Esoteric"),
    UNKNOWN("Unknown"),
}