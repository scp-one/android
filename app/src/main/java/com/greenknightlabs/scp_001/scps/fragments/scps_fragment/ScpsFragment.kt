package com.greenknightlabs.scp_001.scps.fragments.scps_fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.FragmentScpsBinding
import com.greenknightlabs.scp_001.scps.fragments.scps_fragment.adapters.ScpsFragmentAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScpsFragment : BaseFragment<FragmentScpsBinding>(R.layout.fragment_scps) {
    // dependencies
    @Inject lateinit var kairos: Kairos

    // properties
    private val vm: ScpsFragmentViewModel by viewModels()

    // functions
    override fun activityTitle(): String {
        return "Archives"
    }

    override fun menuId(): Int? {
        return null
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return super.onMenuItemSelected(menuItem)
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        val adapter = ScpsFragmentAdapter(vm, kairos)
        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        binding.fragmentScpsRecyclerView.adapter = adapter
        binding.fragmentScpsRecyclerView.layoutManager = layoutManager
        binding.fragmentScpsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (vm.state.value != PageState.Idle) return
                if (layoutManager.findLastVisibleItemPosition() != vm.items.value?.size) return
                vm.paginate(false)
            }
        })

        vm.state.observe(viewLifecycleOwner) {
            (activity as? MainActivity)?.showProgressBar(it == PageState.Fetching)
        }
        vm.toastMessage.observe(viewLifecycleOwner) {
            activity?.makeToast(it)
        }
        vm.didRefresh.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.didRefresh.value = false
                adapter.notifyDataSetChanged()
            }
        }
        vm.didInsertBefore.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.didInsertBefore.value = false
                adapter.notifyItemInserted(0)
            }
        }
        vm.didInsertAfter.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.didInsertAfter.value = false
                adapter.notifyItemInserted(vm.items.value!!.size)
            }
        }
        vm.didDelete.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.didDelete.value = false
//                adapter.notifyItemRemoved(vm.selectedMediaPosition)
//                vm.clearMediaSelection()
            }
        }
    }
}