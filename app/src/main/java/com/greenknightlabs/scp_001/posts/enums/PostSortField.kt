package com.greenknightlabs.scp_001.posts.enums

enum class PostSortField(val rawValue: String) {
    LIKES_COUNT("likesCount"),
    PUBLISHED_AT("publishedAt"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    fun value(): String {
        return this.rawValue
    }

    fun displayName(): String {
        return when (this) {
            LIKES_COUNT -> "Popularity"
            PUBLISHED_AT -> "Published"
            CREATED_AT -> "Created"
            UPDATED_AT -> "Updated"
        }
    }

    companion object {
        fun allCases(): List<PostSortField> {
            return values().toList()
        }

        fun find(rawValue: String): PostSortField? {
            return allCases().firstOrNull { it.rawValue == rawValue }
        }
    }
}