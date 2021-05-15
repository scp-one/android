package com.mirenzen.scp_001.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.coroutineScope
import com.mirenzen.scp_001.R
import com.mirenzen.scp_001.app.activities.MainActivity
import com.mirenzen.scp_001.app.extensions.makeToast
import com.mirenzen.scp_001.app.fragments.BaseFragment
import com.mirenzen.scp_001.app.util.NavMan
import com.mirenzen.scp_001.auth.AuthService
import com.mirenzen.scp_001.auth.dtos.AuthCredentialsDto
import com.mirenzen.scp_001.auth.util.AuthMan
import com.mirenzen.scp_001.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {
    // dependency injections
    @Inject
    lateinit var authService: AuthService
    @Inject
    lateinit var authMan: AuthMan
    @Inject
    lateinit var navMan: NavMan

    // local functions
    override fun activityTitle(): String {
        return ""
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.fragmentLoginButtonForgotPassword.setOnClickListener {
            didTapButtonForgotPassword()
        }
        binding.fragmentLoginButtonRegister.setOnClickListener {
            didTapButtonRegister()
        }
        binding.fragmentLoginButtonLogin.setOnClickListener {
            didTapButtonLogin()
        }
    }

    private fun didTapButtonForgotPassword() {
//        navMan.pushFragment(PassUpdateFragment())
    }

    private fun didTapButtonRegister() {
//        navMan.pushFragment(RegisterFragment())
    }

    private fun didTapButtonLogin() {
        val email = binding.fragmentLoginEditTextEmail.layoutEditTextEmail.text.toString()
        val passw = binding.fragmentLoginEditTextPassword.layoutEditTextPassword.text.toString()
        val dto = AuthCredentialsDto(null, email, passw)

        (activity as? MainActivity)?.lockUI(true)
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            val result = authService.login(dto)
            (activity as? MainActivity)?.lockUI(false)

            if (result.isFailure) {
                result.exceptionOrNull()?.let {
                    activity?.makeToast(it.message)
                }
            } else {
                val accessInfo = result.getOrNull()!!
                authMan.didLogin(accessInfo)
                navMan.reset()
                startActivity(Intent(context, MainActivity::class.java))
                activity?.finish()
            }
        }
    }
}