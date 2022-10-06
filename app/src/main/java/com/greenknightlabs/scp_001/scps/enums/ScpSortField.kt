package com.greenknightlabs.scp_001.scps.enums

enum class ScpSortField(val rawValue: String) {
    UPDATED_AT("updatedAt"),
    NUMBER("number");

    fun value(): String {
        return this.rawValue
    }

    fun displayName(): String {
        return when (this) {
            UPDATED_AT -> "Updated"
            NUMBER -> "Number"
        }
    }

    companion object {
        fun allCases(): List<ScpSortField> {
            return values().toList()
        }

        fun find(rawValue: String): ScpSortField? {
            return allCases().firstOrNull { it.rawValue == rawValue }
        }
    }
}