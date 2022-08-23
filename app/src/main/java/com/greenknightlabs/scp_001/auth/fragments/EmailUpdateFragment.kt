package com.greenknightlabs.scp_001.auth.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.coroutineScope
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.fragments.BaseFragment
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.auth.AuthService
import com.greenknightlabs.scp_001.databinding.FragmentEmailUpdateBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EmailUpdateFragment : BaseFragment<FragmentEmailUpdateBinding>(R.layout.fragment_email_update) {
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
        binding.fragmentEmailUpdateButtonRequest.setOnClickListener {
            didTapButtonRequest()
        }
    }

    private fun didTapButtonRequest() {
        val email = binding.fragmentEmailUpdateEditTextEmail.layoutEditTextEmail.text.toString()

        if (email.isEmpty()) {
            activity?.makeToast("Invalid input.")
            return
        }

        (activity as? MainActivity)?.lockUI(true)

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            try {
                authService.getEmailUpdateMail(email)
                (activity as? MainActivity)?.lockUI(false)
                activity?.makeToast(getString(R.string.alert_email_update_sent))
                navMan.popFragment()
            } catch (e: Throwable) {
                (activity as? MainActivity)?.lockUI(false)
                activity?.makeToast(e.message)
            }
        }
    }
}