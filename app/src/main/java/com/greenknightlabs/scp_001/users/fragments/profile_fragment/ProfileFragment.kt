package com.greenknightlabs.scp_001.users.fragments.profile_fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.play.core.review.ReviewManagerFactory
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.adapters.PageAdapter
import com.greenknightlabs.scp_001.app.config.Constants
import com.greenknightlabs.scp_001.app.enums.MemTrimLevel
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.*
import com.greenknightlabs.scp_001.app.fragments.*
import com.greenknightlabs.scp_001.app.interfaces.BindableView
import com.greenknightlabs.scp_001.app.objects.ListOption
import com.greenknightlabs.scp_001.app.objects.ListOptionSection
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.util.Stash
import com.greenknightlabs.scp_001.auth.AuthService
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.databinding.FragmentProfileBinding
import com.greenknightlabs.scp_001.databinding.LayoutHeaderFragmentProfileBinding
import com.greenknightlabs.scp_001.databinding.LayoutListOptionSectionBinding
import com.greenknightlabs.scp_001.users.UsersService
import com.greenknightlabs.scp_001.users.models.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {
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
        return true
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
    }
}