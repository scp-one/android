package com.greenknightlabs.scp_001.scps.enums

import kotlinx.serialization.Serializable

@Serializable
enum class ScpLoadImages(val rawValue: String) {
    EVERYWHERE("everywhere"),
    IN_VIEW("inView");

    fun value(): String {
        return this.rawValue
    }

    fun displayName(): String {
        return when (this) {
            EVERYWHERE -> "Everywhere"
            IN_VIEW -> "In SCP View"
        }
    }

    companion object {
        fun allCases(): List<ScpLoadImages> {
            return values().toList()
        }

        fun find(rawValue: String): ScpLoadImages? {
            return allCases().firstOrNull { it.rawValue == rawValue }
        }
    }
}