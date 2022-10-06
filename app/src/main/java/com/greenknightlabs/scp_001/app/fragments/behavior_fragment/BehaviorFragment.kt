package com.greenknightlabs.scp_001.app.fragments.behavior_fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.databinding.FragmentBehaviorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BehaviorFragment : BaseFragment<FragmentBehaviorBinding>(R.layout.fragment_behavior) {
    private val vm: BehaviorFragmentViewModel by viewModels()

    // functions
    override fun activityTitle(): String {
        return "Behavior"
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        vm.toastMessage.observe(viewLifecycleOwner) {
            activity?.makeToast(it)
        }
        vm.shouldShowPopupMenu.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.popupMenuPrefKey.value?.displayNames()?.let { displayNames ->
                    activity?.makePopupMenu(vm.popupMenuView.value, displayNames) { index ->
                        vm.popupMenuPrefKey.value?.rawValues()?.getOrNull(index)?.let { rawValue ->
                            vm.preferences.set(vm.popupMenuPrefKey.value!!, rawValue)
                        }
                    }
                }
            }
        }
    }
}