package com.mirenzen.scp_001.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.mirenzen.scp_001.R
import com.mirenzen.scp_001.app.layouts.ListOptionSectionLayout
import com.mirenzen.scp_001.app.util.Preferences
import com.mirenzen.scp_001.databinding.FragmentScrollviewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppearanceFragment : BaseFragment<FragmentScrollviewBinding>(R.layout.fragment_scrollview) {
    // dependency injections
    @Inject
    lateinit var preferences: Preferences

    // view properties


    // local functions
    override fun activityTitle(): String {
        return "Appearance"
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)

        val container = binding.fragmentScrollviewContainer
        val inflater = LayoutInflater.from(binding.root.context)
    }
}