package com.greenknightlabs.scp_001.app.resources.fonts

import com.greenknightlabs.scp_001.app.resources.themes.Themes

enum class FontSizes(val rawValue: String) {
    Small("fontSmall"),
    Regular("fontRegular"),
    Large("fontLarge");

    fun displayName(): String {
        return when (this) {
            Small -> "Small"
            Regular -> "Regular"
            Large -> "Large"
        }
    }

    companion object {
        fun allCases(): List<FontSizes> {
            return values().toList()
        }

        fun find(rawValue: String): FontSizes? {
            return allCases().firstOrNull { it.rawValue == rawValue }
        }
    }
}