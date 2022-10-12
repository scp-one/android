package com.greenknightlabs.scp_001.actions.enums

import com.greenknightlabs.scp_001.scps.enums.ScpSortField

enum class ScpActionsSortField(val rawValue: String) {
    SAVED_AT("savedAt"),
    LIKED_AT("likedAt"),
    READ_AT("readAt");

    fun value(): String {
        return this.rawValue
    }

    fun displayName(): String {
        return when (this) {
            SAVED_AT -> "Date Saved"
            LIKED_AT -> "Date Liked"
            READ_AT -> "Date Read"
        }
    }
}
