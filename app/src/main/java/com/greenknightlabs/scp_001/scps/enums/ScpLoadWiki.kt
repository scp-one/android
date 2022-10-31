package com.greenknightlabs.scp_001.scps.enums

import kotlinx.serialization.Serializable

@Serializable
enum class ScpLoadWiki(val rawValue: String) {
    ALWAYS("always"),
    NEVER("never");

    fun value(): String {
        return this.rawValue
    }

    fun displayName(): String {
        return when (this) {
            ALWAYS -> "Always"
            NEVER -> "No"
        }
    }

    companion object {
        fun allCases(): List<ScpLoadWiki> {
            return values().toList()
        }

        fun find(rawValue: String): ScpLoadWiki? {
            return allCases().firstOrNull { it.rawValue == rawValue }
        }
    }
}
