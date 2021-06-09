package com.mirenzen.scp_001.scps.enums

import kotlinx.serialization.Serializable

@Serializable
enum class ScpClassType(val value: String) {
    SAFE("Safe"),
    EUCLID("Euclid"),
    KETER("Keter")
}