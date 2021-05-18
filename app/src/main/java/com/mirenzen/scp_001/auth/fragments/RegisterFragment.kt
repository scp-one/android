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
import com.mirenzen.scp_001.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(R.layout.fragment_register) {
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
        binding.fragmentRegisterButtonLogin.setOnClickListener {
            didTapButtonLogin()
        }
        binding.fragmentRegisterButtonRegister.setOnClickListener {
            didTapButtonRegister()
        }
    }

    private fun didTapButtonLogin() {
        navMan.popFragment()
    }

    private fun didTapButtonRegister() {
        val usern = binding.fragmentRegisterEditTextUsername.layoutEditTextUsername.text.toString()
        val email = binding.fragmentRegisterEditTextEmail.layoutEditTextEmail.text.toString()
        val passw = binding.fragmentRegisterEditTextPassword.layoutEditTextPassword.text.toString()

        if (usern.isEmpty() || email.isEmpty() || passw.isEmpty()) {
            activity?.makeToast("Invalid input.")
            return
        }

        (activity as? MainActivity)?.lockUI(true)
        val dto = AuthCredentialsDto(usern, email, passw)

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            val result = authService.register(dto)
            (activity as? MainActivity)?.lockUI(false)

            if (result.isFailure) {
                activity?.makeToast(result.exceptionOrNull()!!.message)
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