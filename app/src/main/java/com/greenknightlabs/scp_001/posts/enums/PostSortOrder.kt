package com.greenknightlabs.scp_001.posts.enums

enum class PostSortOrder(val rawValue: String) {
    ASCENDING("asc"),
    DESCENDING("desc");

    fun value(): String {
        return this.rawValue
    }

    fun displayName(field: PostSortField? = null): String {
        return when (this) {
            ASCENDING -> when (field) {
                null -> "Ascending"
                PostSortField.LIKES_COUNT -> "Ascending"
                PostSortField.PUBLISHED_AT -> "Oldest"
                PostSortField.CREATED_AT -> "Oldest"
                PostSortField.UPDATED_AT -> "Oldest"
            }
            DESCENDING -> when (field) {
                null -> "Descending"
                PostSortField.LIKES_COUNT -> "Highest"
                PostSortField.PUBLISHED_AT -> "Newest"
                PostSortField.CREATED_AT -> "Newest"
                PostSortField.UPDATED_AT -> "Newest"
            }
        }
    }

    companion object {
        fun allCases(): List<PostSortOrder> {
            return values().toList()
        }

        fun find(rawValue: String): PostSortOrder? {
            return allCases().firstOrNull { it.rawValue == rawValue }
        }
    }
}