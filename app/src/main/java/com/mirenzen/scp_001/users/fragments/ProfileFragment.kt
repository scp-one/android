package com.mirenzen.scp_001.users.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.mirenzen.scp_001.R
import com.mirenzen.scp_001.app.activities.MainActivity
import com.mirenzen.scp_001.app.adapters.PageAdapter
import com.mirenzen.scp_001.app.enums.MemTrimLevel
import com.mirenzen.scp_001.app.extensions.askConfirmation
import com.mirenzen.scp_001.app.extensions.getView
import com.mirenzen.scp_001.app.extensions.makePopupMenu
import com.mirenzen.scp_001.app.extensions.makeToast
import com.mirenzen.scp_001.app.fragments.PageFragment
import com.mirenzen.scp_001.app.interfaces.BindableView
import com.mirenzen.scp_001.app.layouts.ListOptionSectionLayout
import com.mirenzen.scp_001.app.objects.ListOption
import com.mirenzen.scp_001.app.objects.ListOptionSection
import com.mirenzen.scp_001.app.util.Kairos
import com.mirenzen.scp_001.app.util.NavMan
import com.mirenzen.scp_001.app.util.Stash
import com.mirenzen.scp_001.auth.AuthService
import com.mirenzen.scp_001.auth.util.AuthMan
import com.mirenzen.scp_001.databinding.LayoutHeaderFragmentProfileBinding
import com.mirenzen.scp_001.databinding.LayoutListOptionSectionBinding
import com.mirenzen.scp_001.users.UsersService
import com.mirenzen.scp_001.users.layouts.ProfileFragmentHeaderLayout
import com.mirenzen.scp_001.users.models.User
import com.mirenzen.scp_001.users.view_models.ProfileFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : PageFragment<ListOptionSection, ProfileFragmentViewModel>(),
    ListOptionSectionLayout.Listener,
    ProfileFragmentHeaderLayout.Listener
{
    // dependency injection
    @Inject
    lateinit var usersService: UsersService
    @Inject
    lateinit var authService: AuthService
    @Inject
    lateinit var authMan: AuthMan
    @Inject
    lateinit var navMan: NavMan
    @Inject
    lateinit var kairos: Kairos
    @Inject
    lateinit var stash: Stash

    // view properties
    override val viewModel: ProfileFragmentViewModel by viewModels()
    private val sections = listOf(
        ListOptionSection(
            "APP SETTINGS",
            listOf(
                ListOption("Appearance", R.drawable.ic_palette) {},
                ListOption("Behavior", R.drawable.ic_settings) {}
            )
        ),
        ListOptionSection(
            "GENERAL",
            listOf(
                ListOption("Clear Cache", R.drawable.ic_storage) {
                    activity?.askConfirmation {
                        kairos.trimMemory(MemTrimLevel.FULLY)
                        viewLifecycleOwner.lifecycle.coroutineScope.launch {
                            val result = stash.empty()
                            val error = result.exceptionOrNull()
                            activity?.makeToast( error?.message ?: "Done")
                        }
                    }
                }
            )
        ),
        ListOptionSection(
            "NOTIFICATION SETTINGS",
            listOf(
                ListOption("Push Notifications", R.drawable.ic_notification) {}
            )
        ),
        ListOptionSection(
            "MORE",
            listOf(
                ListOption("Rate Our App", R.drawable.ic_rate) {},
                ListOption("Send Feedback", R.drawable.ic_feedback) {},
                ListOption("Privacy Policy", R.drawable.ic_policy) {},
                ListOption("Licenses", R.drawable.ic_receipt) {}
            )
        )
    )

    // local functions
    override fun activityTitle(): String {
        return "Profile"
    }

    override fun menuId(): Int? {
        return R.menu.menu_fragment_profile
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_fragment_profile_more -> didTapMenuMore(activity?.getView(item.itemId))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun didTapMenuMore(view: View?) {
        val menuItems = listOf("Logout")
        activity?.makePopupMenu(view, menuItems) { index ->
            when (index) {
                0 -> didTapMenuMoreLogout()
            }
        }
    }

    private fun didTapMenuMoreLogout() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            authMan.accessInfo?.let {
                authService.logout(it)
            }
        }
        authMan.didLogout()
        navMan.reset()
        startActivity(Intent(context, MainActivity::class.java))
        activity?.finish()
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        viewModel.items.addAll(sections)
    }

    override fun adapterForView(): PageAdapter<ListOptionSection> {
        return object : PageAdapter<ListOptionSection>(viewModel.items) {
            override val headerOffset = 1

            override fun getItemLayoutId(position: Int): Int {
                return when (position) {
                    0 -> ProfileFragmentHeaderLayout.layoutId()
                    else -> ListOptionSectionLayout.layoutId()
                }
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                when (position) {
                    0 -> (holder as BindableView<User?>).bind(viewModel.user)
                    else -> (holder as BindableView<ListOptionSection>).bind(viewModel.items[position - headerOffset])
                }
            }

            override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return when (viewType) {
                    ProfileFragmentHeaderLayout.layoutId() -> {
                        val binding = LayoutHeaderFragmentProfileBinding
                            .inflate(LayoutInflater.from(parent.context), parent, false)
                        ProfileFragmentHeaderLayout(binding, this@ProfileFragment, kairos)
                    }
                    else -> {
                        val binding = LayoutListOptionSectionBinding
                            .inflate(LayoutInflater.from(parent.context), parent, false)
                        ListOptionSectionLayout(binding, this@ProfileFragment)
                    }
                }
            }
        }
    }

    override fun paginate(refresh: Boolean) {
        (activity as? MainActivity)?.showProgressBar(true)
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            val result = viewModel.paginate(refresh)
            (activity as? MainActivity)?.showProgressBar(false)
            stopRefreshing()
            when (result.isFailure) {
                true -> activity?.makeToast(result.exceptionOrNull()?.message)
                else -> adapter.notifyItemChanged(0)
            }
        }
    }

    // layout event listener
    override fun handleLayoutEvent(
        event: ProfileFragmentHeaderLayout.EventType,
        index: Int,
        view: View?
    ) {
        Timber.d("handling profile header layout event")
    }

    override fun handleLayoutEvent(
        event: ListOptionSectionLayout.EventType,
        sectionIndex: Int,
        optionIndex: Int
    ) {
        viewModel.items.getOrNull(sectionIndex - adapter.headerOffset)
            ?.options?.getOrNull(optionIndex)?.onTapAction?.invoke()
    }
}