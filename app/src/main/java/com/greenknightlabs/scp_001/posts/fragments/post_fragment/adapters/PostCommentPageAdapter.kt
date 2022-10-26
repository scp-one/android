package com.greenknightlabs.scp_001.posts.fragments.post_fragment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.app.adapters.FailToLoadPageComponentViewHolder
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.view_models.PageViewModel
import com.greenknightlabs.scp_001.comments.models.PostComment
import com.greenknightlabs.scp_001.databinding.ComponentFailToLoadPageBinding
import com.greenknightlabs.scp_001.databinding.ComponentLoadCommentsBinding
import com.greenknightlabs.scp_001.databinding.ComponentNoCommentsBinding
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.PostFragmentViewModel
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.view_holders.LoadCommentsComponentViewHolder
import com.greenknightlabs.scp_001.posts.fragments.post_fragment.view_holders.NoCommentsComponentViewHolder

class PostCommentPageAdapter(
    private val vm: PostFragmentViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // properties
    private val loadCommentsViewHolderType = 0
    private val noCommentsViewHolderType = 1
    private val failedToLoadViewHolderType = 2

    // functions
    override fun getItemCount(): Int {
        return 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            loadCommentsViewHolderType -> {
                val binding = ComponentLoadCommentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoadCommentsComponentViewHolder(binding)
            }
            noCommentsViewHolderType -> {
                val binding = ComponentNoCommentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                NoCommentsComponentViewHolder(binding)
            }
            else -> {
                val binding = ComponentFailToLoadPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FailToLoadPageComponentViewHolder<PostComment>(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            loadCommentsViewHolderType -> loadCommentsViewHolderType
            noCommentsViewHolderType -> noCommentsViewHolderType
            else -> failedToLoadViewHolderType
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            loadCommentsViewHolderType -> (holder as LoadCommentsComponentViewHolder).bind(vm, vm)
            noCommentsViewHolderType -> (holder as NoCommentsComponentViewHolder).bind(vm)
            failedToLoadViewHolderType -> (holder as FailToLoadPageComponentViewHolder<PostComment>).bind(vm)
        }
    }

    fun notifyFailedToLoad() {
        notifyItemChanged(failedToLoadViewHolderType)
    }
}
