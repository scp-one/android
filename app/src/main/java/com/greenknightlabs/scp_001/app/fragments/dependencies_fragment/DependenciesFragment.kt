package com.greenknightlabs.scp_001.app.fragments.dependencies_fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.extensions.pushWebView
import com.greenknightlabs.scp_001.app.fragments.BaseFragment
import com.greenknightlabs.scp_001.databinding.FragmentDependenciesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DependenciesFragment : BaseFragment<FragmentDependenciesBinding>(R.layout.fragment_dependencies) {
    private val vm: DependenciesFragmentViewModel by viewModels()

    override fun activityTitle(): String {
        return "Licenses"
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        vm.shouldShowWebView.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.webViewUrl.value?.let { url -> (activity as? MainActivity)?.pushWebView(url) }
            }
        }
    }
}