package com.greenknightlabs.scp_001.comments.enums

enum class PostCommentSortField(val rawValue: String) {
    CREATED_AT("createdAt");

    fun value(): String {
        return this.rawValue
    }

    fun displayName(): String {
        return when (this) {
            CREATED_AT -> "Created"
        }
    }

    companion object {
        fun allCases(): List<PostCommentSortField> {
            return values().toList()
        }

        fun find(rawValue: String): PostCommentSortField? {
            return allCases().firstOrNull { it.rawValue == rawValue }
        }
    }
}
