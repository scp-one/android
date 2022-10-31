package com.greenknightlabs.scp_001.app.util

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.enums.DefaultAppLaunchTab
import com.greenknightlabs.scp_001.app.resources.fonts.FontSizes
import com.greenknightlabs.scp_001.app.resources.themes.Themes
import com.greenknightlabs.scp_001.app.util.preferences.enums.PrefKey
import com.greenknightlabs.scp_001.scps.enums.ScpLoadImages
import com.greenknightlabs.scp_001.scps.enums.ScpLoadWiki
import com.greenknightlabs.scp_001.scps.enums.ScpSortField
import com.greenknightlabs.scp_001.scps.enums.ScpSortOrder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(
    private val context: Context
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

    // behavior
    var defaultLaunchTab: MutableLiveData<DefaultAppLaunchTab>
        private set
    var defaultScpSortField: MutableLiveData<ScpSortField>
        private set
    var defaultScpSortOrder: MutableLiveData<ScpSortOrder>
        private set
    var loadScpImages: MutableLiveData<ScpLoadImages>
        private set
    var loadScpWiki: MutableLiveData<Boolean>
        private set

    init {
        val themeRawValue = readString(PrefKey.Theme)
        theme = MutableLiveData(Themes.find(themeRawValue) ?: Themes.find(PrefKey.Theme.defaultRawValue()))

        val appFontSizeRawValue = readString(PrefKey.AppFontSize)
        appFontSize = MutableLiveData(FontSizes.find(appFontSizeRawValue) ?: FontSizes.find(PrefKey.AppFontSize.defaultRawValue()))

        val scpFontSizeRawValue = readString(PrefKey.ScpFontSize)
        scpFontSize = MutableLiveData(FontSizes.find(scpFontSizeRawValue) ?: FontSizes.find(PrefKey.ScpFontSize.defaultRawValue()))

        val defaultLaunchTabRawValue = readString(PrefKey.DefaultLaunchTab)
        defaultLaunchTab = MutableLiveData(DefaultAppLaunchTab.find(defaultLaunchTabRawValue) ?: DefaultAppLaunchTab.find(PrefKey.DefaultLaunchTab.defaultRawValue()))

        val defaultScpSortFieldRawValue = readString(PrefKey.DefaultScpSortField)
        defaultScpSortField = MutableLiveData(ScpSortField.find(defaultScpSortFieldRawValue) ?: ScpSortField.find(PrefKey.DefaultScpSortField.defaultRawValue()))

        val defaultScpSortOrderRawValue = readString(PrefKey.DefaultScpSortOrder)
        defaultScpSortOrder = MutableLiveData(ScpSortOrder.find(defaultScpSortOrderRawValue) ?: ScpSortOrder.find(PrefKey.DefaultScpSortOrder.defaultRawValue()))

        val loadScpImagesRawValue = readString(PrefKey.LoadScpImages)
        loadScpImages = MutableLiveData(ScpLoadImages.find(loadScpImagesRawValue) ?: ScpLoadImages.find(PrefKey.LoadScpImages.defaultRawValue()))

        val loadScpWikiRawValue = readString(PrefKey.LoadScpWiki)
        val loadScpWikiValue = ScpLoadWiki.find(loadScpWikiRawValue) ?: ScpLoadWiki.find(PrefKey.LoadScpWiki.defaultRawValue())
        loadScpWiki = MutableLiveData(loadScpWikiValue == ScpLoadWiki.ALWAYS)
    }

    fun set(key: PrefKey, rawValue: String) {
        writeString(key, rawValue)

        when (key) {
            PrefKey.Theme -> theme.value = Themes.find(rawValue)
            PrefKey.AppFontSize -> appFontSize.value = FontSizes.find(rawValue)
            PrefKey.ScpFontSize -> scpFontSize.value = FontSizes.find(rawValue)

            PrefKey.DefaultLaunchTab -> defaultLaunchTab.value = DefaultAppLaunchTab.find(rawValue)
            PrefKey.DefaultScpSortField -> defaultScpSortField.value = ScpSortField.find(rawValue)
            PrefKey.DefaultScpSortOrder -> defaultScpSortOrder.value = ScpSortOrder.find(rawValue)
            PrefKey.LoadScpImages -> loadScpImages.value = ScpLoadImages.find(rawValue)
            PrefKey.LoadScpWiki -> loadScpWiki.value = ScpLoadWiki.find(rawValue) == ScpLoadWiki.ALWAYS
        }
    }

    private fun writeString(prefKey: PrefKey, value: String) {
        with (sharedPrefs.edit()) {
            putString(prefKey.rawValue, value)
            apply()
        }
    }

    private fun readString(prefKey: PrefKey): String {
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

    enum class FontScale {
        Sub,
        Body,
        Headline
    }

    fun scpFontSizeDim(scale: FontScale): Float {
        return context.resources.getDimension(when (scpFontSize.value) {
            FontSizes.Small -> when (scale) {
                FontScale.Sub -> R.dimen.fontSizeSmallSub
                FontScale.Body -> R.dimen.fontSizeSmallBody
                FontScale.Headline -> R.dimen.fontSizeSmallHeadline
            }
            FontSizes.Regular -> when (scale) {
                FontScale.Sub -> R.dimen.fontSizeRegularSub
                FontScale.Body -> R.dimen.fontSizeRegularBody
                FontScale.Headline -> R.dimen.fontSizeRegularHeadline
            }
            FontSizes.Large -> when (scale) {
                FontScale.Sub -> R.dimen.fontSizeLargeSub
                FontScale.Body -> R.dimen.fontSizeLargeBody
                FontScale.Headline -> R.dimen.fontSizeLargeHeadline
            }
            else -> 0
        })
    }
}
