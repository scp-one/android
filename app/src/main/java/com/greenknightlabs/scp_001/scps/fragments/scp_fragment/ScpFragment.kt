package com.greenknightlabs.scp_001.scps.fragments.scp_fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.doOnLayout
import androidx.core.view.marginTop
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.config.AppConstants
import com.greenknightlabs.scp_001.app.extensions.getView
import com.greenknightlabs.scp_001.app.extensions.pushWebView
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.app.util.BoomBox
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.*
import com.greenknightlabs.scp_001.scps.models.Scp
import com.greenknightlabs.scp_001.scps.models.ScpContentBlock
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.network.NetworkSchemeHandler
import io.noties.markwon.linkify.LinkifyPlugin
import timber.log.Timber
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

                    binding.componentScpContentBlockCollapsibleTextView.text = block.title ?: "Collapsible block"
                    binding.componentScpContentBlockCollapsibleCardView.setOnClickListener {
                        collapsibleContainer.visibility = when (collapsibleContainer.visibility) {
                            View.GONE -> View.VISIBLE
                            else -> View.GONE
                        }
                    }
                    collapsibleContainer.visibility = View.GONE

                    addComponentsToContainer(markwon, collapsibleContainer, block)
                    block.contentBlocks?.let {
                        renderContentBlocks(markwon, collapsibleContainer, it)
                    }

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
            Timber.d("Setting md content: ${mdContent}")
            val binding: ComponentScpContentBlockMdContentBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_scp_content_block_md_content, container, false)
            markwon.setMarkdown(binding.componentScpContentBlockMdContentTextView, mdContent)
            container.addView(binding.root)
        }

        block.table?.let { table ->
            val tableLayout: ComponentTableBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_table, container, false)

            table.headers?.let { headers ->
                val rowLayout: ComponentTableRowBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_table_row, tableLayout.componentTableLayout, false)

                for (header in headers) {
                    val headerLayout: ComponentTableHeaderBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_table_header, rowLayout.componentTableRow, false)
                    headerLayout.componentTableHeaderText.text = header

                    rowLayout.componentTableRow.addView(headerLayout.root)
                }

                tableLayout.componentTableLayout.addView(rowLayout.root)
            }

            for (row in table.rows) {
                val rowLayout: ComponentTableRowBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_table_row, tableLayout.componentTableLayout, false)

                for (column in row) {
                    val columnLayout: ComponentTableColumnBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_table_column, rowLayout.componentTableRow, false)
                    columnLayout.componentTableColumnText.text = column

                    rowLayout.componentTableRow.addView(columnLayout.root)
                }

                tableLayout.componentTableLayout.addView(rowLayout.root)
            }

            container.addView(tableLayout.root)
        }

        block.audioUrl?.let { audioUrl ->
            val audioComponent: ComponentAudioBinding = DataBindingUtil.inflate(layoutInflater, R.layout.component_audio, container, false)
            audioComponent.url = audioUrl
            audioComponent.boombox = boombox
            container.addView(audioComponent.root)
        }

        block.contentBlocks?.let { subBlocks ->
            renderContentBlocks(markwon, container, subBlocks)
        }
    }
}
