package com.greenknightlabs.scp_001.app.fragments.appearance_fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.databinding.FragmentAppearanceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppearanceFragment : BaseFragment<FragmentAppearanceBinding>(R.layout.fragment_appearance) {
    private val vm: AppearanceFragmentViewModel by viewModels()

    // functions
    override fun activityTitle(): String {
        return "Appearance"
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        vm.toastMessage.observe(viewLifecycleOwner) {
            activity?.makeToast(it)
        }
    }
}