package com.greenknightlabs.scp_001.scps.fragments.scp_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.doOnLayout
import androidx.core.view.marginTop
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.FragmentScpBinding
import com.greenknightlabs.scp_001.scps.models.Scp
import com.greenknightlabs.scp_001.scps.models.ScpContentBlock
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import javax.inject.Inject


@AndroidEntryPoint
class ScpFragment : BaseFragment<FragmentScpBinding>(R.layout.fragment_scp) {
    // dependencies
    @Inject lateinit var kairos: Kairos

    // properties
    private val vm: ScpFragmentViewModel by viewModels()
    var scp: Scp? = null

    // functions
    override fun activityTitle(): String {
        return scp?.name ?: "SCP"
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm
        if (vm.scp.value == null) {
            vm.scp.value = scp
        }

        vm.scp.value?.media?.let { media ->
            kairos.load(media.url).scale(360, 360).default(R.drawable.ic_face).into(binding.fragmentScpImageView)
            binding.fragmentScpImageView.doOnLayout {
                val viewWidth = binding.fragmentScpImageView.measuredWidth
                binding.fragmentScpImageView.layoutParams.height = media.calculateHeight(viewWidth, false)
            }
        }

        vm.scp.value?.contentBlocks?.let { contentBlocks ->
            val markwon = Markwon.builder(requireContext()).build()
            renderContentBlocks(markwon, binding.fragmentScpLinearLayoutContentBlocks, contentBlocks)
        }
    }

    private fun renderContentBlocks(markwon: Markwon, container: LinearLayoutCompat, blocks: List<ScpContentBlock>) {
        blocks.forEach { block ->
            block.mdContent?.let { mdContent ->
                val textView = TextView(context)
                textView.text = markwon.toMarkdown(mdContent)
                container.addView(textView)
            }

            block.table?.let { table ->
                val textView = TextView(context)
                textView.text = "TODO: display table"
                container.addView(textView)
            }

            block.audioUrl?.let { audioUrl ->
                val textView = TextView(context)
                textView.text = "TODO: display audio"
                container.addView(textView)
            }

            block.contentBlocks?.let { subBlocks ->
                val subContainer = LayoutInflater.from(requireContext()).inflate(R.layout.component_scp_content_block, container, true) as LinearLayoutCompat
                renderContentBlocks(markwon, subContainer, subBlocks)
            }
        }
    }
}
