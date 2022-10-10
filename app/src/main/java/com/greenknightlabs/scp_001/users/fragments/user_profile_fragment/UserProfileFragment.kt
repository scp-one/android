package com.greenknightlabs.scp_001.users.fragments.user_profile_fragment

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
import com.greenknightlabs.scp_001.databinding.FragmentUserProfileBinding
import com.greenknightlabs.scp_001.users.fragments.user_profile_fragment.adapters.UserProfileFragmentAdapter
import com.greenknightlabs.scp_001.users.models.User
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileFragment : BaseFragment<FragmentUserProfileBinding>(R.layout.fragment_user_profile) {
    // dependencies
    @Inject lateinit var kairos: Kairos

    // properties
    private val vm: UserProfileFragmentViewModel by viewModels()
    var user: User? = null

    // functions
    override fun activityTitle(): String {
        return "@${vm.user?.username ?: "someone"}"
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

        if (vm.adapter == null) {
            vm.adapter = UserProfileFragmentAdapter(vm, kairos)
        }
        if (vm.user == null) {
            vm.user = user
        }

        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        binding.fragmentUserProfileRecyclerView.adapter = vm.adapter!!
        binding.fragmentUserProfileRecyclerView.layoutManager = layoutManager
        binding.fragmentUserProfileRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (vm.state.value != PageState.Idle) return
                if (layoutManager.findLastVisibleItemPosition() != (vm.items.value?.size ?: 0) - 1) return
                vm.paginate(false)
            }
        })

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
}