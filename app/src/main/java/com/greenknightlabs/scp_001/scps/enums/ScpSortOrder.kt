package com.greenknightlabs.scp_001.scps.enums

enum class ScpSortOrder(val rawValue: String) {
    ASCENDING("asc"),
    DESCENDING("desc");

    fun value(): String {
        return this.rawValue
    }

    fun displayName(field: ScpSortField? = null): String {
        return when (this) {
            ASCENDING -> when (field) {
                null -> "Ascending"
                ScpSortField.UPDATED_AT -> "Oldest"
                ScpSortField.NUMBER -> "Ascending"
            }
            DESCENDING -> when (field) {
                null -> "Descending"
                ScpSortField.UPDATED_AT -> "Newest"
                ScpSortField.NUMBER -> "Descending"
            }
        }
    }

    companion object {
        fun allCases(): List<ScpSortOrder> {
            return values().toList()
        }

        fun find(rawValue: String): ScpSortOrder? {
            return allCases().firstOrNull { it.rawValue == rawValue }
        }
    }
}