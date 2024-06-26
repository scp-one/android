package com.greenknightlabs.scp_001.app.fragments.product_preview_fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.app.fragments.pro_access_fragment.ProAccessFragment
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.shopkeep.objects.ProductProperties
import com.greenknightlabs.scp_001.databinding.FragmentProductPreviewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class ProductPreviewFragment : BaseFragment<FragmentProductPreviewBinding>(R.layout.fragment_product_preview),
    ProductPreviewFragmentViewModel.Listener,
    ProAccessFragment.Listener {
    // interfaces
    interface Listener {
        fun onCompleteSuccessHandler()
    }

    // dependencies
    @Inject lateinit var navMan: NavMan

    // properties
    private val vm: ProductPreviewFragmentViewModel by viewModels()
    var productProperties: ProductProperties? = null
    var listener: Listener? = null

    // functions
    override fun activityTitle(): String {
        return ""
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        if (vm.productProperties == null) {
            vm.productProperties = productProperties
        }
        if (vm.fragmentListener == null) {
            vm.fragmentListener = listener
        }

        vm.vmListener = WeakReference(this)

        val container = binding.fragmentProductPreviewContainer
        if (vm.productProperties?.previewComponentId != null) {
            val previewLayout: ViewDataBinding = DataBindingUtil.inflate(layoutInflater, vm.productProperties!!.previewComponentId!!, container, false)
            container.addView(previewLayout.root)
        }

        if (vm.productProperties != null && vm.productProperties?.unlockedByEntitlement != null) {
            // TODO: switch text and action dependending on entitlement
            binding.fragmentProductPreviewUnlockEntitlementTextView.text = "Unlock this and more with Pro Access"
            binding.fragmentProductPreviewUnlockEntitlementTextView.setOnClickListener {
                val proAccessFragment = ProAccessFragment()
                proAccessFragment.listener = this
                navMan.pushFragment(proAccessFragment, true)
            }
            binding.fragmentProductPreviewUnlockEntitlementTextView.visibility = View.VISIBLE
        } else {
            binding.fragmentProductPreviewUnlockEntitlementTextView.visibility = View.GONE
        }

        binding.fragmentProductPreviewButtonBuy.text = vm.productProperties?.price ?: "Error: Missing Product Properties"

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

    override fun onCompleteSuccess() {
        vm.fragmentListener?.onCompleteSuccessHandler()
        navMan.popFragment()
    }
}
