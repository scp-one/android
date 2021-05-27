package com.mirenzen.scp_001.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.mirenzen.scp_001.R
import com.mirenzen.scp_001.app.extensions.makePopupMenu
import com.mirenzen.scp_001.app.extensions.setTextAsync
import com.mirenzen.scp_001.app.layouts.ListOptionSectionLayout
import com.mirenzen.scp_001.app.util.PrefKey
import com.mirenzen.scp_001.app.util.Preferences
import com.mirenzen.scp_001.databinding.FragmentScrollviewBinding
import com.mirenzen.scp_001.databinding.LayoutPreferenceMultiBinding
import com.mirenzen.scp_001.databinding.LayoutPreferenceToggleBinding
import com.mirenzen.scp_001.databinding.LayoutTitledSectionCardBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AppearanceFragment : BaseFragment<FragmentScrollviewBinding>(R.layout.fragment_scrollview) {
    // dependency injections
    @Inject
    lateinit var preferences: Preferences

    // view properties
    private val prefKeys = listOf(
        PrefKey.Theme,
        PrefKey.FontSize
    )

    // local functions
    override fun activityTitle(): String {
        return "Appearance"
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)

        val container = binding.fragmentScrollviewContainer
        val inflater = LayoutInflater.from(binding.root.context)

        val titledSectionCardBinding = LayoutTitledSectionCardBinding.inflate(inflater, null, false)
        titledSectionCardBinding.layoutTitledSectionCardTextViewTitle.setTextAsync("USER INTERFACE")

        for (prefKey in prefKeys) {
            val listedValues = prefKey.listedValues()
            if (listedValues.size < 2) {
                val toggleLayout = LayoutPreferenceToggleBinding.inflate(inflater, null, false)
                toggleLayout.layoutPreferenceToggleTextViewName.setTextAsync(prefKey.value)
                toggleLayout.layoutPreferenceToggleSwitchToggle.isChecked = preferences.readBoolean(prefKey, false)
                toggleLayout.layoutPreferenceToggleSwitchToggle.setOnCheckedChangeListener { _, isChecked ->
                    preferences.set(prefKey, isChecked)
                }

                titledSectionCardBinding.layoutTitledSectionCardContainer.addView(toggleLayout.root)
            } else {
                val multiLayout = LayoutPreferenceMultiBinding.inflate(inflater, null, false)
                multiLayout.layoutPreferenceMultiTextViewName.setTextAsync(prefKey.value)
                multiLayout.layoutPreferenceMultiTextViewValue.setTextAsync(preferences.readString(prefKey, "Default"))
                multiLayout.layoutPreferenceMultiTextViewValue.setOnClickListener {
                    activity?.makePopupMenu(it, listedValues) { index ->
                        preferences.set(prefKey, listedValues[index])
                        multiLayout.layoutPreferenceMultiTextViewValue.setTextAsync(listedValues[index])
                    }
                }

                titledSectionCardBinding.layoutTitledSectionCardContainer.addView(multiLayout.root)
            }
        }

        container.addView(titledSectionCardBinding.root)
    }
}