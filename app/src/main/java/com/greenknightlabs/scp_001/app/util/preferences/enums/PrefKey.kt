package com.greenknightlabs.scp_001.app.util.preferences.enums

import com.greenknightlabs.scp_001.app.resources.fonts.FontSizes
import com.greenknightlabs.scp_001.app.resources.themes.Themes
import com.greenknightlabs.scp_001.app.util.Preferences

enum class PrefKey(val rawValue: String) {
    Theme("Theme"),
    AppFontSize("appFontSize"),
    ScpFontSize("scpFontSize");

    fun rawValues(): List<String> {
        return when (this) {
            Theme -> Themes.allCases().map { it.rawValue }
            AppFontSize -> FontSizes.allCases().map { it.rawValue }
            ScpFontSize -> FontSizes.allCases().map { it.rawValue }
        }
    }

    fun defaultRawValue(): String {
        return when (this) {
            Theme -> Themes.Dark.rawValue
            AppFontSize -> FontSizes.Regular.rawValue
            ScpFontSize -> FontSizes.Small.rawValue
        }
    }

    fun displayName(): String {
        return when (this) {
            Theme -> "Theme"
            AppFontSize -> "App Font Size"
            ScpFontSize -> "SCP Font Size"
        }
    }

    fun displayNames(): List<String> {
        return when (this) {
            Theme -> Themes.allCases().map { it.displayName() }
            AppFontSize -> FontSizes.allCases().map { it.displayName() }
            ScpFontSize -> FontSizes.allCases().map { it.displayName() }
        }
    }

//    fun currentValueDisplayName(): String {
//        return when (this) {
//            Theme -> Preferences.
//        }
//    }

//    fun productProperties(rawValue: String) -> P
}