package com.greenknightlabs.scp_001.app.resources.themes

import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.resources.fonts.FontSizes
import com.greenknightlabs.scp_001.app.util.shopkeep.config.ShopkeepConstants
import com.greenknightlabs.scp_001.app.util.shopkeep.objects.ProductProperties
import com.greenknightlabs.scp_001.users.enums.UserEntitlements

enum class Themes(
    val rawValue: String,
    private val resIdFontSmall: Int,
    private val resIdFontRegular: Int,
    private val resIdFontLarge: Int,
) {
    Slate(
        "themeSlate",
        R.style.Theme_Slate_FontSmall,
        R.style.Theme_Slate_FontRegular,
        R.style.Theme_Slate_FontLarge
    ),
    Light(
        "themeLight",
        R.style.Theme_Light_FontSmall,
        R.style.Theme_Light_FontRegular,
        R.style.Theme_Light_FontLarge
    ),
    Retrolight(
      "themeRetrolight",
      R.style.Theme_Retrolight_FontSmall,
      R.style.Theme_Retrolight_FontRegular,
      R.style.Theme_Retrolight_FontLarge
    ),
    Dark(
        "themeDark",
        R.style.Theme_Dark_FontSmall,
        R.style.Theme_Dark_FontRegular,
        R.style.Theme_Dark_FontLarge
    ),
    Midnight(
        "themeMidnight",
        R.style.Theme_Midnight_FontSmall,
        R.style.Theme_Midnight_FontRegular,
        R.style.Theme_Midnight_FontLarge
    ),
    Void(
        "themeVoid",
        R.style.Theme_Void_FontSmall,
        R.style.Theme_Void_FontRegular,
        R.style.Theme_Void_FontLarge
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
            Slate -> "Slate"
            Light -> "Light"
            Retrolight -> "Retrolight" + ShopkeepConstants.PREMIUM_INDICATOR
            Dark -> "Dark" + ShopkeepConstants.PREMIUM_INDICATOR
            Midnight -> "Midnight" + ShopkeepConstants.PREMIUM_INDICATOR
            Void -> "Void" + ShopkeepConstants.PREMIUM_INDICATOR
        }
    }

    fun productProperties(): ProductProperties? {
        return when (this) {
            Retrolight -> ProductProperties(
                "",
                "$1.99",
                UserEntitlements.Pro,
                R.layout.component_preview_theme_retrolight
            )
            Dark -> ProductProperties(
                "",
                "$1.99",
                UserEntitlements.Pro,
                null,
            )
            Midnight -> ProductProperties(
                "",
                "$1.99",
                UserEntitlements.Pro,
                null,
            )
            Void -> ProductProperties(
                "",
                "$1.99",
                UserEntitlements.Pro,
                null
            )
            else -> null
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
