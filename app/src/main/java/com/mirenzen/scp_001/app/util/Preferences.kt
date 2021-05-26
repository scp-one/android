package com.mirenzen.scp_001.app.util

import android.content.Context
import com.mirenzen.scp_001.R
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

enum class PrefKey(val value: String) {
    Theme("Theme"),
    FontSize("Font Size"),
    HideBarOnScroll("Hide Bar On Scroll");

    fun listedValues(): List<String> {
        return when (this) {
            Theme -> enumValues<Themes>().map { it.value }
            FontSize -> enumValues<FontSizes>().map { it.value }
            HideBarOnScroll -> listOf(this.value)
        }
    }
}

enum class FontSizes(val value: String) {
    Small("Small"),
    Regular("Regular"),
    Large("Large");

    companion object {
        fun find(value: String): FontSizes? {
            return values().firstOrNull { it.value == value }
        }
    }
}

enum class Themes(
    val value: String,
    private val resIdFontSmall: Int,
    private val resIdFontRegular: Int,
    private val resIdFontLarge: Int,
) {
    Dark(
        "Dark",
        R.style.Theme_Dark_FontSmall,
        R.style.Theme_Dark_FontRegular,
        R.style.Theme_Dark_FontLarge
    ),
    Light(
        "Light",
        R.style.Theme_Light_FontSmall,
        R.style.Theme_Light_FontRegular,
        R.style.Theme_Light_FontLarge
    ),
    Nessie(
        "Nessie",
        R.style.Theme_Nessie_FontSmall,
        R.style.Theme_Nessie_FontRegular,
        R.style.Theme_Nessie_FontLarge
    );

    fun resId(fontSize: FontSizes): Int {
        return when (fontSize) {
            FontSizes.Small -> this.resIdFontSmall
            FontSizes.Regular -> this.resIdFontRegular
            FontSizes.Large -> this.resIdFontLarge
        }
    }

    companion object {
        fun find(value: String): Themes? {
            return values().firstOrNull { it.value == value }
        }
    }
}

@Singleton
class Preferences @Inject constructor(
    context: Context
) {
    private val sharedPrefs = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

    var theme: Themes = Themes.find(readString(PrefKey.Theme, Themes.Dark.value))!!
        set(value) {
            field = value
            writeString(PrefKey.Theme, value.value)
        }

    var fontSize: FontSizes = FontSizes.find(readString(PrefKey.FontSize, FontSizes.Regular.value))!!
        set(value) {
            field = value
            writeString(PrefKey.FontSize, value.value)
        }

    var hideBarOnScroll: Boolean = readBoolean(PrefKey.HideBarOnScroll, false)
        set(value) {
            field = value
            writeBoolean(PrefKey.HideBarOnScroll, value)
        }

    private fun writeString(prefKey: PrefKey, value: String) {
        with (sharedPrefs.edit()) {
            putString(prefKey.value, value)
            apply()
        }
    }

    private fun writeBoolean(prefKey: PrefKey, value: Boolean) {
        with (sharedPrefs.edit()) {
            putBoolean(prefKey.value, value)
            apply()
        }
    }

    private fun readString(prefKey: PrefKey, default: String): String {
        return sharedPrefs.getString(prefKey.value, default)!!
    }

    private fun readBoolean(prefKey: PrefKey, default: Boolean): Boolean {
        return sharedPrefs.getBoolean(prefKey.value, default)
    }
}