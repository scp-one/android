package com.greenknightlabs.scp_001.scps.fragments.scp_fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import coil.load
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.config.AppConstants
import com.greenknightlabs.scp_001.app.extensions.screenWidth
import com.greenknightlabs.scp_001.app.extensions.getView
import com.greenknightlabs.scp_001.app.extensions.pushWebView
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.app.util.BoomBox
import com.greenknightlabs.scp_001.databinding.*
import com.greenknightlabs.scp_001.scps.models.Scp
import com.greenknightlabs.scp_001.scps.models.ScpContentBlock
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.network.NetworkSchemeHandler
import javax.inject.Inject


@AndroidEntryPoint
class ScpFragment : BaseFragment<FragmentScpBinding>(R.layout.fragment_scp) {
    // dependencies
    @Inject lateinit var boombox: BoomBox

    // properties
    private val vm: ScpFragmentViewModel by viewModels()
    var scp: Scp? = null

    // functions
    override fun activityTitle(): String {
        return vm.scp.value?.name ?: "SCP"
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
            val screenWidth = activity?.screenWidth() ?: 0
            binding.fragmentScpImageView.layoutParams.height = media.calculateHeight(screenWidth, false)
            binding.fragmentScpImageView.load(media.url) {
                size(360)
                crossfade(true)
                error(R.drawable.ic_cancel)
            }
        }
        vm.scp.value?.contentBlocks?.let { contentBlocks ->
            val markwon = Markwon.builder(requireContext())
                .usePlugin(object : AbstractMarkwonPlugin() {
                    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                        builder.linkResolver { _, link ->
                            if (link.startsWith("http")) {
                                vm.webViewUrl.value = link
                                vm.shouldShowWebView.value = true
                            } else {
                                vm.webViewUrl.value = "${AppConstants.WIKI_URL}$link"
                                vm.shouldShowWebView.value = true
                            }
                        }

                        super.configureConfiguration(builder)
                    }
                })
                .usePlugin(ImagesPlugin.create {
                    it.addSchemeHandler(NetworkSchemeHandler.create())
                })
                .build()

            renderContentBlocks(markwon, binding.fragmentScpLinearLayoutContentBlocks, contentBlocks)
        }
    }

    private fun renderContentBlocks(markwon: Markwon, container: LinearLayoutCompat, blocks: List<ScpContentBlock>) {
        for (block in blocks) {
            when (block.collapsible) {
                true -> {
                    val binding: ComponentScpContentBlockCollapsibleBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_scp_content_block_collapsible, container, false)
                    val collapsibleContainer = binding.componentScpContentBlockCollapsibleContainer

                    binding.preferences = vm.preferences
                    binding.componentScpContentBlockCollapsibleTextView.text = block.title ?: "Collapsible block"
                    binding.componentScpContentBlockCollapsibleCardView.setOnClickListener {
                        collapsibleContainer.visibility = when (collapsibleContainer.visibility) {
                            View.GONE -> View.VISIBLE
                            else -> View.GONE
                        }
                    }
                    collapsibleContainer.visibility = View.GONE

                    addComponentsToContainer(markwon, collapsibleContainer, block)

                    container.addView(binding.root)
                }
                else -> {
                    addComponentsToContainer(markwon, container, block)
                }
            }
        }
    }

    private fun addComponentsToContainer(markwon: Markwon, container: LinearLayoutCompat, block: ScpContentBlock) {
        block.mdContent?.let { mdContent ->
            val mdContentBinding: ComponentScpContentBlockMdContentBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_scp_content_block_md_content, container, false)

            mdContentBinding.preferences = vm.preferences
            markwon.setMarkdown(mdContentBinding.componentScpContentBlockMdContentTextView, mdContent)
            container.addView(mdContentBinding.root)
        }

        block.table?.let { table ->
            val tableLayoutBinding: ComponentTableBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_table, container, false)

            table.headers?.let { headers ->
                val rowLayoutBinding: ComponentTableRowBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_table_row, tableLayoutBinding.componentTableLayout, false)

                for (header in headers) {
                    val headerLayout: ComponentTableHeaderBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_table_header, rowLayoutBinding.componentTableRow, false)

                    headerLayout.preferences = vm.preferences
                    headerLayout.componentTableHeaderText.text = header
                    rowLayoutBinding.componentTableRow.addView(headerLayout.root)
                }

                tableLayoutBinding.componentTableLayout.addView(rowLayoutBinding.root)
            }

            for (row in table.rows) {
                val rowLayoutBinding: ComponentTableRowBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_table_row, tableLayoutBinding.componentTableLayout, false)

                for (column in row) {
                    val columnLayoutBinding: ComponentTableColumnBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_table_column, rowLayoutBinding.componentTableRow, false)

                    columnLayoutBinding.preferences = vm.preferences
                    columnLayoutBinding.componentTableColumnText.text = column
                    rowLayoutBinding.componentTableRow.addView(columnLayoutBinding.root)
                }

                tableLayoutBinding.componentTableLayout.addView(rowLayoutBinding.root)
            }

            container.addView(tableLayoutBinding.root)
        }

        block.audioUrl?.let { audioUrl ->
            val audioComponentBinding: ComponentAudioBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_audio, container, false)

            audioComponentBinding.url = audioUrl
            audioComponentBinding.boombox = boombox
            container.addView(audioComponentBinding.root)
        }

        block.contentBlocks?.let { subBlocks ->
            renderContentBlocks(markwon, container, subBlocks)
        }
    }
}
