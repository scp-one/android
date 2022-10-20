package com.greenknightlabs.scp_001.app.util.preferences.enums

import com.greenknightlabs.scp_001.app.enums.DefaultAppLaunchTab
import com.greenknightlabs.scp_001.app.resources.fonts.FontSizes
import com.greenknightlabs.scp_001.app.resources.themes.Themes
import com.greenknightlabs.scp_001.app.util.shopkeep.objects.ProductProperties
import com.greenknightlabs.scp_001.scps.enums.ScpLoadImages
import com.greenknightlabs.scp_001.scps.enums.ScpSortField
import com.greenknightlabs.scp_001.scps.enums.ScpSortOrder

enum class PrefKey(val rawValue: String) {
    Theme("Theme"),
    AppFontSize("appFontSize"),
    ScpFontSize("scpFontSize"),

    DefaultScpSortField("defaultScpSortField"),
    DefaultScpSortOrder("defaultScpSortOrder"),
    LoadScpImages("loadScpImages"),
    DefaultLaunchTab("defaultLaunchTab");

    fun rawValues(): List<String> {
        return when (this) {
            Theme -> Themes.allCases().map { it.rawValue }
            AppFontSize -> FontSizes.allCases().map { it.rawValue }
            ScpFontSize -> FontSizes.allCases().map { it.rawValue }

            DefaultScpSortField -> ScpSortField.allCases().map { it.rawValue }
            DefaultScpSortOrder -> ScpSortOrder.allCases().map { it.rawValue }
            LoadScpImages -> ScpLoadImages.allCases().map { it.rawValue }
            DefaultLaunchTab -> DefaultAppLaunchTab.allCases().map { it.rawValue }
        }
    }

    fun defaultRawValue(): String {
        return when (this) {
            Theme -> Themes.Dark.rawValue
            AppFontSize -> FontSizes.Regular.rawValue
            ScpFontSize -> FontSizes.Small.rawValue

            DefaultScpSortField -> ScpSortField.NUMBER.rawValue
            DefaultScpSortOrder -> ScpSortOrder.ASCENDING.rawValue
            LoadScpImages -> ScpLoadImages.EVERYWHERE.rawValue
            DefaultLaunchTab -> DefaultAppLaunchTab.ARCHIVES.rawValue
        }
    }

    fun displayName(): String {
        return when (this) {
            Theme -> "Theme"
            AppFontSize -> "App Font Size"
            ScpFontSize -> "SCP Font Size"

            DefaultScpSortField -> "Default Sort Field"
            DefaultScpSortOrder -> "Default Sort Order"
            LoadScpImages -> "Load SCP Images"
            DefaultLaunchTab -> "Default Launch Tab"
        }
    }

    fun displayNames(): List<String> {
        return when (this) {
            Theme -> Themes.allCases().map { it.displayName() }
            AppFontSize -> FontSizes.allCases().map { it.displayName() }
            ScpFontSize -> FontSizes.allCases().map { it.displayName() }

            DefaultScpSortField -> ScpSortField.allCases().map { it.displayName() }
            DefaultScpSortOrder -> ScpSortOrder.allCases().map { it.displayName() }
            LoadScpImages -> ScpLoadImages.allCases().map { it.displayName() }
            DefaultLaunchTab -> DefaultAppLaunchTab.allCases().map { it.displayName() }
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
