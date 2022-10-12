package com.greenknightlabs.scp_001.scps.fragments.scp_fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.doOnLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.extensions.getView
import com.greenknightlabs.scp_001.app.extensions.pushWebView
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.app.util.BoomBox
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.ComponentAudioBinding
import com.greenknightlabs.scp_001.databinding.ComponentScpContentBlockBinding
import com.greenknightlabs.scp_001.databinding.ComponentScpContentBlockMdContentBinding
import com.greenknightlabs.scp_001.databinding.FragmentScpBinding
import com.greenknightlabs.scp_001.scps.models.Scp
import com.greenknightlabs.scp_001.scps.models.ScpContentBlock
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.network.NetworkSchemeHandler
import javax.inject.Inject


@AndroidEntryPoint
class ScpFragment : BaseFragment<FragmentScpBinding>(R.layout.fragment_scp) {
    // dependencies
    @Inject lateinit var kairos: Kairos
    @Inject lateinit var boombox: BoomBox

    // properties
    private val vm: ScpFragmentViewModel by viewModels()
    var scp: Scp? = null

    // functions
    override fun activityTitle(): String {
        return scp?.name ?: "SCP"
    }

    override fun menuId(): Int? {
        return R.menu.menu_fragment_scp
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        val view = activity?.getView(menuItem.itemId)
        when (menuItem.itemId) {
            R.id.menu_fragment_scp_more -> vm.handleOnTapMenuMore(view)
        }
        return false
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm
        if (vm.scp.value == null) {
            vm.scp.value = scp
        }

        vm.shouldShowWebView.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.shouldShowWebView.value = false
                vm.webViewUrl.value?.let { url -> activity?.pushWebView(url) }
            }
        }
        vm.scp.value?.media?.let { media ->
            kairos.load(media.url).scale(360, 360).default(R.drawable.ic_face).into(binding.fragmentScpImageView)
            binding.fragmentScpImageView.doOnLayout {
                val viewWidth = binding.fragmentScpImageView.measuredWidth
                binding.fragmentScpImageView.layoutParams.height = media.calculateHeight(viewWidth, false)
            }
        }

        vm.scp.value?.contentBlocks?.let { contentBlocks ->
            val markwon = Markwon.builder(requireContext())
                .usePlugin(ImagesPlugin.create {
                    it.addSchemeHandler(NetworkSchemeHandler.create())
                })
                .build()
            renderContentBlocks(markwon, binding.fragmentScpLinearLayoutContentBlocks, contentBlocks)
        }
    }

    private fun renderContentBlocks(markwon: Markwon, container: LinearLayoutCompat, blocks: List<ScpContentBlock>) {
        blocks.forEach { block ->
            block.mdContent?.let { mdContent ->
                val binding: ComponentScpContentBlockMdContentBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_scp_content_block_md_content, container, false)
                markwon.setMarkdown(binding.componentScpContentBlockMdContentTextView, mdContent)
                container.addView(binding.root)
            }

            block.table?.let { table ->
                val textView = TextView(context)
                textView.text = "TODO: display table"
                container.addView(textView)
            }

            block.audioUrl?.let { audioUrl ->
                val audioComponent: ComponentAudioBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_audio, container, false)
                audioComponent.url = audioUrl
                audioComponent.boombox = boombox
                container.addView(audioComponent.root)
            }

            block.contentBlocks?.let { subBlocks ->
                val subContainer: ComponentScpContentBlockBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_scp_content_block, container, false)
                renderContentBlocks(markwon, subContainer.componentScpContentBlockContainer, subBlocks)
            }
        }
    }
}
