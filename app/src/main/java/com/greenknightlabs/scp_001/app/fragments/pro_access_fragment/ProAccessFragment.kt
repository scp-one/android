package com.greenknightlabs.scp_001.app.fragments.pro_access_fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.databinding.FragmentProAccessBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class ProAccessFragment : BaseFragment<FragmentProAccessBinding>(R.layout.fragment_pro_access), ProAccessFragmentViewModel.Listener {
    // interfaces
    interface Listener {
        fun onCompleteSuccess()
    }

    // dependencies
    @Inject lateinit var navMan: NavMan

    // properties
    private val vm: ProAccessFragmentViewModel by viewModels()
    var listener: Listener? = null

    // functions
    override fun activityTitle(): String {
        return ""
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        vm.vmListener = WeakReference(this)
        if (vm.fragmentListener == null) {
            vm.fragmentListener = listener
        }

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
        vm.hasUnlockedProAccess.observe(viewLifecycleOwner) {
            if (vm.fragmentListener != null && it == true) {
                vm.fragmentListener?.onCompleteSuccess()
                navMan.popFragment()
            }
        }
    }

    override fun provideActivity(): MainActivity? {
        return activity as? MainActivity
    }
}
