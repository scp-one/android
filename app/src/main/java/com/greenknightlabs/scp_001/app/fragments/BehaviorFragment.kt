//package com.greenknightlabs.scp_001.app.fragments
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import com.greenknightlabs.scp_001.R
//import com.greenknightlabs.scp_001.app.extensions.makePopupMenu
//import com.greenknightlabs.scp_001.app.extensions.setTextAsync
//import com.greenknightlabs.scp_001.app.util.PrefKey
//import com.greenknightlabs.scp_001.app.util.Preferences
//import com.greenknightlabs.scp_001.databinding.FragmentScrollviewBinding
//import com.greenknightlabs.scp_001.databinding.LayoutPreferenceMultiBinding
//import com.greenknightlabs.scp_001.databinding.LayoutPreferenceToggleBinding
//import com.greenknightlabs.scp_001.databinding.LayoutTitledSectionCardBinding
//import dagger.hilt.android.AndroidEntryPoint
//import javax.inject.Inject
//
//@AndroidEntryPoint
//class BehaviorFragment : BaseFragment<FragmentScrollviewBinding>(R.layout.fragment_scrollview) {
//    // dependency injections
//    @Inject
//    lateinit var preferences: Preferences
//
//    // view properties
//    private val prefKeys = listOf(
//        PrefKey.HideBarOnScroll
//    )
//
//    // local functions
//    override fun activityTitle(): String {
//        return "Behavior"
//    }
//
//    override fun configureView(view: View, savedInstanceState: Bundle?) {
//        super.configureView(view, savedInstanceState)
//
//        val container = binding.fragmentScrollviewContainer
//        val inflater = LayoutInflater.from(binding.root.context)
//
//        val titledSectionCardBinding = LayoutTitledSectionCardBinding.inflate(inflater, null, false)
//        titledSectionCardBinding.layoutTitledSectionCardTextViewTitle.setTextAsync("APP BEHAVIOR")
//
//        for (prefKey in prefKeys) {
//            val listedValues = prefKey.listedValues()
//            if (listedValues.size < 2) {
//                val toggleLayout = LayoutPreferenceToggleBinding.inflate(inflater, null, false)
//                toggleLayout.layoutPreferenceToggleTextViewName.setTextAsync(prefKey.value)
//                toggleLayout.layoutPreferenceToggleSwitchToggle.isChecked = preferences.readBoolean(prefKey, false)
//                toggleLayout.layoutPreferenceToggleSwitchToggle.setOnCheckedChangeListener { _, isChecked ->
//                    preferences.set(prefKey, isChecked)
//                }
//
//                titledSectionCardBinding.layoutTitledSectionCardContainer.addView(toggleLayout.root)
//            } else {
//                val multiLayout = LayoutPreferenceMultiBinding.inflate(inflater, null, false)
//                multiLayout.layoutPreferenceMultiTextViewName.setTextAsync(prefKey.value)
//                multiLayout.layoutPreferenceMultiTextViewValue.setTextAsync(preferences.readString(prefKey, "Default"))
//                multiLayout.layoutPreferenceMultiTextViewValue.setOnClickListener {
//                    activity?.makePopupMenu(it, listedValues) { index ->
//                        preferences.set(prefKey, listedValues[index])
//                        multiLayout.layoutPreferenceMultiTextViewValue.setTextAsync(listedValues[index])
//                    }
//                }
//
//                titledSectionCardBinding.layoutTitledSectionCardContainer.addView(multiLayout.root)
//            }
//        }
//
//        container.addView(titledSectionCardBinding.root)
//    }
//}