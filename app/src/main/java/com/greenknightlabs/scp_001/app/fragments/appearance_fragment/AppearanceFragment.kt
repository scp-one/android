package com.greenknightlabs.scp_001.app.fragments.appearance_fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.app.resources.fonts.FontSizes
import com.greenknightlabs.scp_001.app.resources.themes.Themes
import com.greenknightlabs.scp_001.app.util.Preferences
import com.greenknightlabs.scp_001.databinding.FragmentAppearanceBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppearanceFragment : BaseFragment<FragmentAppearanceBinding>(R.layout.fragment_appearance) {
    // dependencies
    @Inject lateinit var preferences: Preferences

    // properties
    private val vm: AppearanceFragmentViewModel by viewModels()

    private lateinit var currentTheme: Themes
    private lateinit var currentAppFontSize: FontSizes
    private lateinit var currentScpFontSize: FontSizes

    // functions
    override fun activityTitle(): String {
        return "Appearance"
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        currentTheme = preferences.theme.value!!
        currentAppFontSize = preferences.appFontSize.value!!
        currentScpFontSize = preferences.scpFontSize.value!!

        vm.toastMessage.observe(viewLifecycleOwner) {
            activity?.makeToast(it)
        }

        preferences.theme.observe(viewLifecycleOwner) {
            if (currentTheme != it) { activity?.recreate() }
        }
        preferences.appFontSize.observe(viewLifecycleOwner) {
            if (currentAppFontSize != it) { activity?.recreate() }
        }
        preferences.scpFontSize.observe(viewLifecycleOwner) {
            if (currentScpFontSize != it) { activity?.recreate() }
        }
    }
}
