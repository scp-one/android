package com.greenknightlabs.scp_001.comments.enums

enum class PostCommentSortOrder(val rawValue: String) {
    ASCENDING("asc"),
    DESCENDING("desc");

    fun value(): String {
        return this.rawValue
    }

    fun displayName(field: PostCommentSortField? = null): String {
        return when (this) {
            ASCENDING -> when (field) {
                null -> "Ascending"
                PostCommentSortField.CREATED_AT -> "Oldest"
            }
            DESCENDING -> when (field) {
                null -> "Descending"
                PostCommentSortField.CREATED_AT -> "Newest"
            }
        }
    }

    companion object {
        fun allCases(): List<PostCommentSortOrder> {
            return values().toList()
        }

        fun find(rawValue: String): PostCommentSortOrder? {
            return allCases().firstOrNull { it.rawValue == rawValue }
        }
    }
}
