package com.greenknightlabs.scp_001.media.fragments.media_collection_fragment.adapters

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.databinding.ComponentMediaBinding
import com.greenknightlabs.scp_001.media.fragments.media_collection_fragment.MediaCollectionFragmentViewModel
import com.greenknightlabs.scp_001.media.models.Media


class MediaCollectionFragmentAdapter(
    private val vm: MediaCollectionFragmentViewModel,
) : RecyclerView.Adapter<MediaCollectionFragmentAdapter.MediaComponentViewHolder>() {
    // view holder
    class MediaComponentViewHolder(
        private val binding: ComponentMediaBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        // functions
        fun bind(position: Int, adapter: MediaCollectionFragmentAdapter) {
            val media = adapter.vm.items.value!![position]

            binding.vm = adapter.vm
            binding.media = media
            binding.componentMediaImageView.setOnClickListener {
                adapter.vm.handleOnTapMedia(position)
            }
            binding.componentMediaImageView.load(media.url) {
                size(240)
                crossfade(true)
                error(R.drawable.ic_cancel)
            }
        }
    }

    // properties
    private lateinit var binding: ComponentMediaBinding
    private var selectedMediaPosition = -1

    // functions
    override fun getItemCount(): Int {
        return vm.items.value?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaComponentViewHolder {
        binding = ComponentMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaComponentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaComponentViewHolder, position: Int) {
        holder.bind(position, this)
    }

    fun handleSelectedMediaChanged(position: Int) {
        if (position == selectedMediaPosition) {
            notifyItemChanged(position)
        } else {
            notifyItemChanged(selectedMediaPosition)
            notifyItemChanged(position)
        }

        selectedMediaPosition = position
    }
}
