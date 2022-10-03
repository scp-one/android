package com.greenknightlabs.scp_001.users.fragments.account_fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.askConfirmation
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.fragments.BaseFragment
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.FragmentAccountBinding
import com.greenknightlabs.scp_001.users.models.User
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.Contexts.getApplication
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment(
    val user: MutableLiveData<User?>,
) : BaseFragment<FragmentAccountBinding>(R.layout.fragment_account) {
    @Inject lateinit var kairos: Kairos

    private val vm: AccountFragmentViewModel by viewModels()

    // functions
    override fun activityTitle(): String {
        return "Account"
    }

    override fun menuId(): Int? {
        return R.menu.menu_fragment_account
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_fragment_account_save -> vm.save(user)
        }
        return super.onMenuItemSelected(menuItem)
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        vm.nickname.value = user.value?.nickname ?: ""

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm

        vm.state.observe(viewLifecycleOwner) {
            (activity as? MainActivity)?.showProgressBar(it == PageState.Fetching)
        }
        vm.toastMessage.observe(viewLifecycleOwner) {
            activity?.makeToast(it)
        }
        vm.shouldShowConfirmAlert.observe(viewLifecycleOwner) {
            if (it == true) {
                activity?.askConfirmation { vm.confirmAlertAction.value?.invoke() }
            }
        }
        user.observe(viewLifecycleOwner) {
            kairos.load(it?.avatarUrl).scale(240, 240).default(R.drawable.default_avatar).into(binding.fragmentAccountAvatarImageView)
        }
    }
}