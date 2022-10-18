package com.greenknightlabs.scp_001.app.fragments.tip_jar_fragment

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.databinding.FragmentTipJarBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

@AndroidEntryPoint
class TipJarFragment : BaseFragment<FragmentTipJarBinding>(R.layout.fragment_tip_jar), TipJarFragmentViewModel.Listener {
    // properties
    private val vm: TipJarFragmentViewModel by viewModels()

    // functions
    override fun activityTitle(): String {
        return ""
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        vm.listener = WeakReference(this)
    }

    override fun provideActivity(): MainActivity? {
        return activity as? MainActivity
    }
}
