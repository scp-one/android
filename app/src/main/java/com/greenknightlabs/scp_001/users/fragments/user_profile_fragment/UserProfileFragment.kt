package com.greenknightlabs.scp_001.users.fragments.user_profile_fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.adapters.PageAdapter
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.askConfirmation
import com.greenknightlabs.scp_001.app.extensions.getView
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.extensions.screenWidth
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.databinding.FragmentUserProfileBinding
import com.greenknightlabs.scp_001.users.fragments.user_profile_fragment.adapters.UserProfileFragmentItemsAdapter
import com.greenknightlabs.scp_001.users.fragments.user_profile_fragment.adapters.UserProfileFragmentHeaderAdapter
import com.greenknightlabs.scp_001.users.models.User
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileFragment : BaseFragment<FragmentUserProfileBinding>(R.layout.fragment_user_profile) {
    // dependencies
    @Inject lateinit var authMan: AuthMan

    // properties
    private val vm: UserProfileFragmentViewModel by viewModels()
    var user: User? = null

    // functions
    override fun activityTitle(): String {
        return "@${vm.user?.username ?: "someone"}"
    }

    override fun menuId(): Int? {
        return R.menu.menu_fragment_user_profile
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateMenu(menu, menuInflater)
        val menuItemAdd = menu.getItem(1)

        if (user?.id != null && authMan.payload?.id != null) {
            menuItemAdd?.isVisible = user!!.id == authMan.payload!!.id
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        val view = activity?.getView(menuItem.itemId)
        when (menuItem.itemId) {
            R.id.menu_fragment_user_profile_sort -> vm.handleOnTapMenuSort(view)
            R.id.menu_fragment_user_profile_post -> vm.handleOnTapMenuPost()
        }

        return false
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        if (vm.adapter == null) {
            val userProfileFragmentHeaderAdapter = UserProfileFragmentHeaderAdapter(vm)
            val userProfileFragmentItemsAdapter = UserProfileFragmentItemsAdapter(vm)
            val pageAdapter = PageAdapter(vm)
            vm.headerAdapter = userProfileFragmentHeaderAdapter
            vm.itemsAdapter = userProfileFragmentItemsAdapter
            vm.pageAdapter = pageAdapter
            vm.adapter = ConcatAdapter(userProfileFragmentHeaderAdapter, userProfileFragmentItemsAdapter, pageAdapter)
        }
        if (vm.user == null) {
            vm.user = user
            vm.onRefreshAction()
        }

        vm.itemsAdapter?.screenWidth = activity?.screenWidth() ?: 0

        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        binding.fragmentUserProfileRecyclerView.adapter = vm.adapter!!
        binding.fragmentUserProfileRecyclerView.layoutManager = layoutManager
        binding.fragmentUserProfileRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (vm.state.value != PageState.Idle) return
                if (layoutManager.findLastVisibleItemPosition() != (vm.items.value?.size ?: 0)) return
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
        vm.shouldShowConfirmAlert.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.shouldShowConfirmAlert.value = false
                activity?.askConfirmation { vm.confirmAlertAction.value?.invoke() }
            }
        }
        vm.failedToLoad.observe(viewLifecycleOwner) {
            vm.pageAdapter!!.notifyItemChanged(0)
        }
    }
}
