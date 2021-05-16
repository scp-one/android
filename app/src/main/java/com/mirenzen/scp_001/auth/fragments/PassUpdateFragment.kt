package com.mirenzen.scp_001.auth.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.coroutineScope
import com.mirenzen.scp_001.R
import com.mirenzen.scp_001.app.activities.MainActivity
import com.mirenzen.scp_001.app.extensions.makeToast
import com.mirenzen.scp_001.app.fragments.BaseFragment
import com.mirenzen.scp_001.app.util.NavMan
import com.mirenzen.scp_001.auth.AuthService
import com.mirenzen.scp_001.databinding.FragmentPassUpdateBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PassUpdateFragment : BaseFragment<FragmentPassUpdateBinding>(R.layout.fragment_pass_update) {
    // dependency injections
    @Inject
    lateinit var authService: AuthService
    @Inject
    lateinit var navMan: NavMan

    // local functions
    override fun activityTitle(): String {
        return ""
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.fragmentPassUpdateButtonRequest.setOnClickListener {
            didTapButtonRequest()
        }
    }

    private fun didTapButtonRequest() {
        val email = binding.fragmentPassUpdateEditTextEmail.layoutEditTextEmail.text.toString()

        (activity as? MainActivity)?.lockUI(true)
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            val result = authService.getPasswordUpdateMail(email)
            (activity as? MainActivity)?.lockUI(false)

            if (result.isFailure) {
                activity?.makeToast(result.exceptionOrNull()!!.message)
            } else {
                activity?.makeToast(getString(R.string.alert_pass_update_sent))
                navMan.popFragment()
            }
        }
    }
}