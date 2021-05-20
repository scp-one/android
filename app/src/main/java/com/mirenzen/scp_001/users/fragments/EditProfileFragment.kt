package com.mirenzen.scp_001.users.fragments

import android.os.Bundle
import android.view.View
import com.mirenzen.scp_001.R
import com.mirenzen.scp_001.app.fragments.BaseFragment
import com.mirenzen.scp_001.app.util.Kairos
import com.mirenzen.scp_001.app.util.NavMan
import com.mirenzen.scp_001.auth.fragments.PassUpdateFragment
import com.mirenzen.scp_001.auth.util.AuthMan
import com.mirenzen.scp_001.databinding.FragmentEditProfileBinding
import com.mirenzen.scp_001.users.UsersService
import com.mirenzen.scp_001.users.models.User
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment(private val user: User) : BaseFragment<FragmentEditProfileBinding>(R.layout.fragment_edit_profile) {
    // dependency injection
    @Inject
    lateinit var usersService: UsersService
    @Inject
    lateinit var authMan: AuthMan
    @Inject
    lateinit var navMan: NavMan
    @Inject
    lateinit var kairos: Kairos

    // local functions
    override fun activityTitle(): String {
        return "Edit Profile"
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.fragmentEditProfileEditTextNickname.layoutEditTextNickname.setText(user.nickname)
        binding.fragmentEditProfileEditTextUsername.layoutEditTextUsername.setText(user.username)
        binding.fragmentEditProfileEditTextUsername.layoutEditTextUsername.isEnabled = false
        binding.layoutEditProfileEditEmailTextView.setOnClickListener {
            Timber.d("on click email")
        }
        binding.layoutEditProfileEditPasswordTextView.setOnClickListener {
            navMan.pushFragment(PassUpdateFragment())
        }

        kairos.load(user.avatarUrl)
            .default(R.drawable.ic_header_scientist)
            .scale(480, 480)
            .into(binding.fragmentEditProfileAvatarImageView)
    }
}