package com.greenknightlabs.scp_001.users.fragments.advanced_account_fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.databinding.FragmentAdvancedAccountBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdvancedAccountFragment : BaseFragment<FragmentAdvancedAccountBinding>(R.layout.fragment_advanced_account) {
    // properties
    private val vm: AdvancedAccountFragmentViewModel by viewModels()

    // functions
    override fun activityTitle(): String {
        return "Advanced Settings"
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm
    }
}