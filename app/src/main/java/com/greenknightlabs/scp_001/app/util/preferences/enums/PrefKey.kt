package com.greenknightlabs.scp_001.app.util.preferences.enums

import com.greenknightlabs.scp_001.app.enums.DefaultAppLaunchTab
import com.greenknightlabs.scp_001.app.resources.fonts.FontSizes
import com.greenknightlabs.scp_001.app.resources.themes.Themes
import com.greenknightlabs.scp_001.app.util.shopkeep.objects.ProductProperties
import com.greenknightlabs.scp_001.scps.enums.ScpLoadImages
import com.greenknightlabs.scp_001.scps.enums.ScpLoadWiki
import com.greenknightlabs.scp_001.scps.enums.ScpSortField
import com.greenknightlabs.scp_001.scps.enums.ScpSortOrder

enum class PrefKey(val rawValue: String) {
    Theme("theme"),
    AppFontSize("appFontSize"),
    ScpFontSize("scpFontSize"),

    DefaultLaunchTab("defaultLaunchTab"),
    DefaultScpSortField("defaultScpSortField"),
    DefaultScpSortOrder("defaultScpSortOrder"),
    LoadScpImages("loadScpImages"),
    LoadScpWiki("loadScpWiki");

    fun rawValues(): List<String> {
        return when (this) {
            Theme -> Themes.allCases().map { it.rawValue }
            AppFontSize -> FontSizes.allCases().map { it.rawValue }
            ScpFontSize -> FontSizes.allCases().map { it.rawValue }

            DefaultLaunchTab -> DefaultAppLaunchTab.allCases().map { it.rawValue }
            DefaultScpSortField -> ScpSortField.allCases().map { it.rawValue }
            DefaultScpSortOrder -> ScpSortOrder.allCases().map { it.rawValue }
            LoadScpImages -> ScpLoadImages.allCases().map { it.rawValue }
            LoadScpWiki -> ScpLoadWiki.allCases().map { it.rawValue }
        }
    }

    fun defaultRawValue(): String {
        return when (this) {
            Theme -> Themes.Slate.rawValue
            AppFontSize -> FontSizes.Regular.rawValue
            ScpFontSize -> FontSizes.Small.rawValue

            DefaultLaunchTab -> DefaultAppLaunchTab.ARCHIVES.rawValue
            DefaultScpSortField -> ScpSortField.NUMBER.rawValue
            DefaultScpSortOrder -> ScpSortOrder.ASCENDING.rawValue
            LoadScpImages -> ScpLoadImages.EVERYWHERE.rawValue
            LoadScpWiki -> ScpLoadWiki.NEVER.rawValue
        }
    }

    fun displayName(): String {
        return when (this) {
            Theme -> "Theme"
            AppFontSize -> "App Font Size"
            ScpFontSize -> "SCP Font Size"

            DefaultLaunchTab -> "Default Launch Tab"
            DefaultScpSortField -> "Default Sort Field"
            DefaultScpSortOrder -> "Default Sort Order"
            LoadScpImages -> "Load SCP Images"
            LoadScpWiki -> "Automatically Open In Wiki"
        }
    }

    fun displayNames(): List<String> {
        return when (this) {
            Theme -> Themes.allCases().map { it.displayName() }
            AppFontSize -> FontSizes.allCases().map { it.displayName() }
            ScpFontSize -> FontSizes.allCases().map { it.displayName() }

            DefaultLaunchTab -> DefaultAppLaunchTab.allCases().map { it.displayName() }
            DefaultScpSortField -> ScpSortField.allCases().map { it.displayName() }
            DefaultScpSortOrder -> ScpSortOrder.allCases().map { it.displayName() }
            LoadScpImages -> ScpLoadImages.allCases().map { it.displayName() }
            LoadScpWiki -> ScpLoadWiki.allCases().map { it.displayName() }
        }
    }

    fun productProperties(rawValue: String): ProductProperties? {
        return when (this) {
            Theme -> {
                Themes.find(rawValue)?.productProperties()
            }
            else -> null
        }
    }
}
