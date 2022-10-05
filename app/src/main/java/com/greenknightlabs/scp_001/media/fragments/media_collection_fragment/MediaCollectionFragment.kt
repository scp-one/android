package com.greenknightlabs.scp_001.media.fragments.media_collection_fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.fragments.BaseFragment
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.FragmentMediaCollectionBinding
import com.greenknightlabs.scp_001.media.fragments.media_collection_fragment.adapters.MediaCollectionFragmentAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MediaCollectionFragment : BaseFragment<FragmentMediaCollectionBinding>(R.layout.fragment_media_collection) {
    @Inject lateinit var kairos: Kairos

    private val vm: MediaCollectionFragmentViewModel by viewModels()

    // functions
    override fun activityTitle(): String {
        return "Media"
    }

    override fun menuId(): Int? {
        return R.menu.menu_fragment_media_collection
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_fragment_media_collection_add -> handleOnTapMenuAdd()
        }
        return false
    }

    private fun handleOnTapMenuAdd() {

    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)

        val adapter = MediaCollectionFragmentAdapter(vm, kairos)
        val layoutManager = GridLayoutManager(activity, 3, RecyclerView.VERTICAL, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.fragmentMediaCollectionRecyclerView.adapter = adapter
        binding.fragmentMediaCollectionRecyclerView.layoutManager = layoutManager

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
        vm.didInsert.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.didInsert.value = false
                adapter.notifyItemInserted(vm.items.value!!.size)
            }
        }
        vm.selectedMedia.observe(viewLifecycleOwner) {
            adapter.handleSelectedMediaChanged(vm.selectedMediaPosition)
        }
    }
}