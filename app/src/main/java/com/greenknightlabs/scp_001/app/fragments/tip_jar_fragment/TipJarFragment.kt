package com.greenknightlabs.scp_001.app.fragments.tip_jar_fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.makeToast
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

        vm.isLocked.observe(viewLifecycleOwner) {
            (activity as? MainActivity)?.lockUI(it)
        }
        vm.state.observe(viewLifecycleOwner) {
            (activity as? MainActivity)?.showProgressBar(it == PageState.Fetching)
        }
        vm.toastMessage.observe(viewLifecycleOwner) {
            if (it != null) {
                activity?.makeToast(it)
                vm.toastMessage.value = null
            }
        }
    }

    override fun provideActivity(): MainActivity? {
        return activity as? MainActivity
    }
}
