package com.greenknightlabs.scp_001.users.fragments.profile_fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.config.Constants
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.*
import com.greenknightlabs.scp_001.app.fragments.*
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {
    @Inject lateinit var  kairos: Kairos

    private val vm: ProfileFragmentViewModel by viewModels()

    // functions
    override fun activityTitle(): String {
        return "Profile"
    }

    override fun menuId(): Int? {
        return R.menu.menu_fragment_profile
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_fragment_profile_more -> didTapMenuMore(activity?.getView(menuItem.itemId))
        }
        return super.onMenuItemSelected(menuItem)
    }

    private fun didTapMenuMore(view: View?) {
        val menuItems = listOf("Logout")
        activity?.makePopupMenu(view, menuItems) { index ->
            when (index) {
                0 -> vm.handleOnTapLogout(context, activity as? MainActivity)
            }
        }
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
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
        vm.shouldShowPrivacyPolicy.observe(viewLifecycleOwner) {
            if (it == true) {
                activity?.pushWebView(Constants.URL_PRIVACY_POLICY)
                vm.shouldShowPrivacyPolicy.value = false
            }
        }
        vm.shouldShowTermsOfService.observe(viewLifecycleOwner) {
            if (it == true) {
                activity?.pushWebView(Constants.URL_TERMS_OF_SERVICE)
                vm.shouldShowTermsOfService.value = false
            }
        }
        vm.user.observe(viewLifecycleOwner) {
            kairos.load(it?.avatarUrl).scale(240, 240).default(R.drawable.default_avatar).into(binding.layoutHeaderFragmentProfile.layoutHeaderFragmentProfileAvatar)
        }
    }
}