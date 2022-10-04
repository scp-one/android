package com.greenknightlabs.scp_001.app.util

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.greenknightlabs.scp_001.app.resources.fonts.FontSizes
import com.greenknightlabs.scp_001.app.resources.themes.Themes
import com.greenknightlabs.scp_001.app.util.preferences.enums.PrefKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(
    context: Context
) {
    // dependencies
    private val sharedPrefs = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

    // properties
    // appearance
    var theme: MutableLiveData<Themes>
        private set
    var appFontSize: MutableLiveData<FontSizes>
        private set
    var scpFontSize: MutableLiveData<FontSizes>
        private set

    init {
        val themeRawValue = readString(PrefKey.Theme)
        theme = MutableLiveData(Themes.find(themeRawValue) ?: Themes.find(PrefKey.Theme.defaultRawValue()))

        val appFontSizeRawValue = readString(PrefKey.AppFontSize)
        appFontSize = MutableLiveData(FontSizes.find(appFontSizeRawValue) ?: FontSizes.find(PrefKey.AppFontSize.defaultRawValue()))

        val scpFontSizeRawValue = readString(PrefKey.ScpFontSize)
        scpFontSize = MutableLiveData(FontSizes.find(scpFontSizeRawValue) ?: FontSizes.find(PrefKey.ScpFontSize.defaultRawValue()))
    }

    fun set(key: PrefKey, rawValue: String) {
        writeString(key, rawValue)

        when (key) {
            PrefKey.Theme -> theme.value = Themes.find(rawValue)
            PrefKey.AppFontSize -> appFontSize.value = FontSizes.find(rawValue)
            PrefKey.ScpFontSize -> scpFontSize.value = FontSizes.find(rawValue)
        }
    }

    private fun writeString(prefKey: PrefKey, value: String) {
        with (sharedPrefs.edit()) {
            putString(prefKey.rawValue, value)
            apply()
        }
    }

    fun readString(prefKey: PrefKey): String {
        return sharedPrefs.getString(prefKey.rawValue, prefKey.defaultRawValue())!!
    }

    fun currentDisplayName(prefKey: PrefKey): String {
        val currentRawValue = readString(prefKey)
        val indexOfRawValue = prefKey.rawValues().indexOf(currentRawValue)
        val currentDisplayName = when (indexOfRawValue != -1) {
            true -> prefKey.displayNames()[indexOfRawValue]
            else -> "Default"
        }
        return currentDisplayName
    }
}