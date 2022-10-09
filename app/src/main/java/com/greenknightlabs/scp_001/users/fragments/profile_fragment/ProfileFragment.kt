package com.greenknightlabs.scp_001.users.fragments.profile_fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.play.core.review.ReviewManagerFactory
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.config.AppConstants
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.*
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
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

        view?.makePopupMenu(menuItems) { index ->
            when (index) {
                0 -> vm.handleOnTapLogout(activity as? MainActivity)
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
                vm.shouldShowConfirmAlert.value = false
                activity?.askConfirmation { vm.confirmAlertAction.value?.invoke() }
            }
        }
        vm.shouldShowRateApp.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.shouldShowRateApp.value = false
                showInAppReview()
            }
        }
        vm.shouldShowShareApp.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.shouldShowShareApp.value = false
                showShareSheet()
            }
        }
        vm.shouldShowWebView.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.shouldShowWebView.value = false
                vm.webViewUrl.value?.let { url -> activity?.pushWebView(url) }
            }
        }
        vm.user.observe(viewLifecycleOwner) {
            kairos.load(it?.avatarUrl).scale(240, 240).default(R.drawable.default_avatar).into(binding.layoutHeaderFragmentProfile.layoutHeaderFragmentProfileAvatar)
        }
    }

    private fun showInAppReview() {
        val context = context ?: return
        val activity = activity ?: return

        val reviewManager = ReviewManagerFactory.create(context)
        val requestReviewFlow = reviewManager.requestReviewFlow()

        requestReviewFlow.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                val reviewInfo = request.result
                val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener {
                    // do something if needed
                }
            } else {
                Timber.d("Error: ${request.exception.toString()}")
                vm.toastMessage.value = "Could not request a review at this time"
            }
        }
    }

    private fun showShareSheet() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, AppConstants.APP_STORE_URL)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}