package com.greenknightlabs.scp_001.media.enums

enum class MediaSortOrder(val rawValue: String) {
    Ascending("asc"),
    Descending("desc");

    fun value(): String {
        return this.rawValue
    }

    fun displayName(field: MediaSortField? = null): String {
        return when (this) {
            Ascending -> when (field) {
                null -> "Ascending"
                MediaSortField.CreatedAt -> "Oldest"
            }
            Descending -> when (field) {
                null -> "Descending"
                MediaSortField.CreatedAt -> "Newest"
            }
        }
    }

    companion object {
        fun allCases(): List<MediaSortOrder> {
            return values().toList()
        }
    }
}