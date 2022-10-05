package com.greenknightlabs.scp_001.media.enums

enum class MediaSortField(val rawValue: String) {
    CreatedAt("createdAt");

    fun value(): String {
        return this.rawValue
    }

    fun displayName(): String {
        return when (this) {
            CreatedAt -> "Created"
        }
    }

    companion object {
        fun allCases(): List<MediaSortField> {
            return values().toList()
        }
    }
}