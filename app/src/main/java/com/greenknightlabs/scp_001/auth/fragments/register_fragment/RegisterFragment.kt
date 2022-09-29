package com.greenknightlabs.scp_001.auth.fragments.register_fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.config.Constants
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.extensions.pushWebView
import com.greenknightlabs.scp_001.app.fragments.BaseFragment
import com.greenknightlabs.scp_001.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(R.layout.fragment_register) {
    // dependencies
    private val vm: RegisterFragmentViewModel by viewModels()

    // functions
    override fun activityTitle(): String {
        return ""
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        vm.isLocked.observe(viewLifecycleOwner) {
            (activity as? MainActivity)?.lockUI(it)
        }
        vm.state.observe(viewLifecycleOwner) {
            (activity as? MainActivity)?.showProgressBar(it == PageState.Fetching)
        }
        vm.toastMessage.observe(viewLifecycleOwner) {
            activity?.makeToast(it)
        }
        vm.shouldPresentTermsAndConditions.observe(viewLifecycleOwner) {
            if (it == true) {
                activity?.pushWebView(Constants.URL_TERMS_OF_SERVICE)
                vm.shouldPresentTermsAndConditions.value = false
            }
        }
        vm.shouldResetActivity.observe(viewLifecycleOwner) {
            if (it == true) {
                startActivity(Intent(context, MainActivity::class.java))
                activity?.finish()
            }
        }
    }
}