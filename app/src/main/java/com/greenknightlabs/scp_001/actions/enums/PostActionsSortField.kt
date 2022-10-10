package com.greenknightlabs.scp_001.actions.enums

enum class PostActionsSortField(val rawValue: String) {
    LIKED_AT("likedAt");

    fun value(): String {
        return this.rawValue
    }

    fun displayName(): String {
        return when (this) {
            LIKED_AT -> "Date Liked"
        }
    }
}