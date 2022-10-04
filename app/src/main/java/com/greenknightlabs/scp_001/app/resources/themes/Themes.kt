package com.greenknightlabs.scp_001.app.resources.themes

import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.resources.fonts.FontSizes

enum class Themes(
    val rawValue: String,
    private val resIdFontSmall: Int,
    private val resIdFontRegular: Int,
    private val resIdFontLarge: Int,
) {
    Dark(
        "themeDark",
        R.style.Theme_Dark_FontSmall,
        R.style.Theme_Dark_FontRegular,
        R.style.Theme_Dark_FontLarge
    ),
    Light(
        "themeLight",
        R.style.Theme_Light_FontSmall,
        R.style.Theme_Light_FontRegular,
        R.style.Theme_Light_FontLarge
    ),
    Midnight(
        "themeMidnight",
        R.style.Theme_Midnight_FontSmall,
        R.style.Theme_Midnight_FontRegular,
        R.style.Theme_Midnight_FontLarge
    );

    fun resId(fontSize: FontSizes): Int {
        return when (fontSize) {
            FontSizes.Small -> this.resIdFontSmall
            FontSizes.Regular -> this.resIdFontRegular
            FontSizes.Large -> this.resIdFontLarge
        }
    }

    fun displayName(): String {
        return when (this) {
            Light -> "Light"
            Dark -> "Dark"
            Midnight -> "Midnight"
        }
    }

    companion object {
        fun allCases(): List<Themes> {
            return values().toList()
        }

        fun find(rawValue: String): Themes? {
            return allCases().firstOrNull { it.rawValue == rawValue }
        }
    }
}