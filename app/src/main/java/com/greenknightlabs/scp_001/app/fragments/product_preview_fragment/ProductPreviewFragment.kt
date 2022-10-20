package com.greenknightlabs.scp_001.app.fragments.product_preview_fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.app.util.shopkeep.objects.ProductProperties
import com.greenknightlabs.scp_001.databinding.ComponentTableBinding
import com.greenknightlabs.scp_001.databinding.FragmentProductPreviewBinding
import com.greenknightlabs.scp_001.media.fragments.media_collection_fragment.MediaCollectionFragment
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

@AndroidEntryPoint
class ProductPreviewFragment : BaseFragment<FragmentProductPreviewBinding>(R.layout.fragment_product_preview), ProductPreviewFragmentViewModel.Listener {
    // interfaces
    interface Listener {
        fun onCompleteSuccessHandler()
    }

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

        binding.fragmentProductPreviewButtonBuy.text = vm.productProperties?.price ?: "Error"

        vm.isLocked.observe(viewLifecycleOwner) {
            (activity as? MainActivity)?.lockUI(it)
        }
        vm.state.observe(viewLifecycleOwner) {
            (activity as? MainActivity)?.showProgressBar(it == PageState.Fetching)
        }
        vm.toastMessage.observe(viewLifecycleOwner) {
            activity?.makeToast(it)
        }
    }

    override fun provideActivity(): MainActivity? {
        return activity as? MainActivity
    }
}
