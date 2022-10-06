package com.greenknightlabs.scp_001.app.enums

import com.greenknightlabs.scp_001.scps.enums.ScpSortField

enum class DefaultAppLaunchTab(val rawValue: String) {
    ARCHIVES("archives"),
    POSTS("posts"),
    BOOKMARKS("bookmarks"),
    PROFILE("profile");

    fun value(): String {
        return this.rawValue
    }

    fun displayName(): String {
        return when (this) {
            ARCHIVES -> "Archives"
            POSTS -> "Posts"
            BOOKMARKS -> "Bookmarks"
            PROFILE -> "Profile"
        }
    }

    companion object {
        fun allCases(): List<DefaultAppLaunchTab> {
            return values().toList()
        }

        fun find(rawValue: String): DefaultAppLaunchTab? {
            return allCases().firstOrNull { it.rawValue == rawValue }
        }
    }
}