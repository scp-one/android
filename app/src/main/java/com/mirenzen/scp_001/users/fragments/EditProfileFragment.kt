package com.mirenzen.scp_001.users.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.coroutineScope
import com.mirenzen.scp_001.R
import com.mirenzen.scp_001.app.activities.MainActivity
import com.mirenzen.scp_001.app.extensions.makeToast
import com.mirenzen.scp_001.app.fragments.BaseFragment
import com.mirenzen.scp_001.app.layouts.ListOptionSectionLayout
import com.mirenzen.scp_001.app.objects.ListOption
import com.mirenzen.scp_001.app.objects.ListOptionSection
import com.mirenzen.scp_001.app.util.Kairos
import com.mirenzen.scp_001.app.util.NavMan
import com.mirenzen.scp_001.auth.fragments.EmailUpdateFragment
import com.mirenzen.scp_001.auth.fragments.PassUpdateFragment
import com.mirenzen.scp_001.auth.util.AuthMan
import com.mirenzen.scp_001.databinding.FragmentEditProfileBinding
import com.mirenzen.scp_001.databinding.LayoutListOptionSectionBinding
import com.mirenzen.scp_001.users.UsersService
import com.mirenzen.scp_001.users.dtos.EditUserDto
import com.mirenzen.scp_001.users.models.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment(private val user: User) : BaseFragment<FragmentEditProfileBinding>(R.layout.fragment_edit_profile), ListOptionSectionLayout.Listener {
    // dependency injection
    @Inject
    lateinit var usersService: UsersService
    @Inject
    lateinit var authMan: AuthMan
    @Inject
    lateinit var navMan: NavMan
    @Inject
    lateinit var kairos: Kairos

    // view properties
    val section = ListOptionSection(
        "ACCOUNT SETTINGS",
        listOf(
            ListOption("Change Email", R.drawable.ic_email) {
                navMan.pushFragment(EmailUpdateFragment(), hideNavBar = true)
            },
            ListOption("Change Password", R.drawable.ic_lock) {
                navMan.pushFragment(PassUpdateFragment(), hideNavBar = true)
            }
        )
    )

    // local functions
    override fun activityTitle(): String {
        return "Account"
    }

    override fun menuId(): Int? {
        return R.menu.menu_fragment_edit_profile
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_fragment_edit_profile_save -> didTapMenuSave()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.fragmentEditProfileAvatarImageView.setOnClickListener {
            didTapImageAvatar()
        }
        binding.fragmentEditProfileEditTextNickname.layoutEditTextNickname.setText(user.nickname)

        val container = binding.fragmentEditProfileViewContainer
        val sectionBinding = LayoutListOptionSectionBinding
            .inflate(LayoutInflater.from(binding.root.context), null, false)
        val sectionLayout = ListOptionSectionLayout(sectionBinding, this)
        container.addView(sectionLayout.itemView)
        sectionLayout.bind(section)

        kairos.load(user.avatarUrl)
            .default(R.drawable.ic_header_scientist)
            .scale(480, 480)
            .into(binding.fragmentEditProfileAvatarImageView)
    }

    private fun didTapImageAvatar() {
        Timber.d("tapped avatar image")
    }

    private fun didTapMenuSave() {
        val nickname = binding.fragmentEditProfileEditTextNickname.layoutEditTextNickname.text.toString()

        if (nickname.isEmpty()) {
            activity?.makeToast("Invalid input.")
            return
        }

        (activity as? MainActivity)?.lockUI(true)
        val dto = EditUserDto(nickname, null, null)

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            val result = usersService.editUser(user.username, dto)
            (activity as? MainActivity)?.lockUI(false)

            if (result.isFailure) {
                activity?.makeToast(result.exceptionOrNull()!!.message)
            } else {
                activity?.makeToast("Updated.")
            }
        }
    }

    override fun handleLayoutEvent(
        event: ListOptionSectionLayout.EventType,
        sectionIndex: Int,
        optionIndex: Int
    ) {
        section.options.getOrNull(optionIndex)?.onTapAction?.invoke()
    }
}