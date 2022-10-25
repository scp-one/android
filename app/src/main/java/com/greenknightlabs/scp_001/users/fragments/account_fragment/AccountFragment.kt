package com.greenknightlabs.scp_001.users.fragments.account_fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import coil.load
import coil.transform.CircleCropTransformation
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.askConfirmation
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.databinding.FragmentAccountBinding
import com.greenknightlabs.scp_001.users.fragments.profile_fragment.ProfileFragmentViewModel
import com.greenknightlabs.scp_001.users.models.User
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding>(R.layout.fragment_account) {
    // properties
    private val vm: AccountFragmentViewModel by viewModels()
    var user: MutableLiveData<User?>? = null

    // functions
    override fun activityTitle(): String {
        return "Account"
    }

    override fun menuId(): Int? {
        return R.menu.menu_fragment_account
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_fragment_account_save -> vm.user?.let { vm.save(it) }
        }
        return super.onMenuItemSelected(menuItem)
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm
        if (vm.user == null) {
            vm.user = user
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
        vm.shouldShowConfirmAlert.observe(viewLifecycleOwner) {
            if (it == true) {
                activity?.askConfirmation { vm.confirmAlertAction.value?.invoke() }
                vm.shouldShowConfirmAlert.value = false
            }
        }
        vm.avatarUrl.observe(viewLifecycleOwner) {
            binding.fragmentAccountAvatarImageView.load(it) {
                size(240)
                transformations(CircleCropTransformation())
                crossfade(true)
                error(R.drawable.default_avatar)
            }
        }
        vm.user?.observe(viewLifecycleOwner) {
            if (it != null) {
                vm.nickname.value = it.nickname
                if (vm.avatarUrl.value == null) {
                    vm.avatarUrl.value = it.avatarUrl
                }
            }
        }
    }
}
