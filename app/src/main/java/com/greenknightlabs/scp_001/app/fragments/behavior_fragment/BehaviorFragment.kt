package com.greenknightlabs.scp_001.app.fragments.behavior_fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.enums.DefaultAppLaunchTab
import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.app.util.Preferences
import com.greenknightlabs.scp_001.databinding.FragmentBehaviorBinding
import com.greenknightlabs.scp_001.scps.enums.ScpLoadImages
import com.greenknightlabs.scp_001.scps.enums.ScpSortField
import com.greenknightlabs.scp_001.scps.enums.ScpSortOrder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BehaviorFragment : BaseFragment<FragmentBehaviorBinding>(R.layout.fragment_behavior) {
    // dependencies
    @Inject lateinit var preferences: Preferences

    // properties
    private val vm: BehaviorFragmentViewModel by viewModels()

    private lateinit var currentDefaultAppLaunchTab: DefaultAppLaunchTab
    private lateinit var currentDefaultScpSortField: ScpSortField
    private lateinit var currentDefaultScpSortOrder: ScpSortOrder
    private lateinit var currentLoadScpImages: ScpLoadImages

    // functions
    override fun activityTitle(): String {
        return "Behavior"
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        currentDefaultAppLaunchTab = preferences.defaultLaunchTab.value!!
        currentDefaultScpSortField = preferences.defaultScpSortField.value!!
        currentDefaultScpSortOrder = preferences.defaultScpSortOrder.value!!
        currentLoadScpImages = preferences.loadScpImages.value!!

        vm.toastMessage.observe(viewLifecycleOwner) {
            activity?.makeToast(it)
        }

        preferences.defaultLaunchTab.observe(viewLifecycleOwner) {
            if (currentDefaultAppLaunchTab != it) { activity?.recreate() }
        }
        preferences.defaultScpSortField.observe(viewLifecycleOwner) {
            if (currentDefaultScpSortField != it) { activity?.recreate() }
        }
        preferences.defaultScpSortOrder.observe(viewLifecycleOwner) {
            if (currentDefaultScpSortOrder != it) { activity?.recreate() }
        }
        preferences.loadScpImages.observe(viewLifecycleOwner) {
            if (currentLoadScpImages != it) { activity?.recreate() }
        }
    }
}
