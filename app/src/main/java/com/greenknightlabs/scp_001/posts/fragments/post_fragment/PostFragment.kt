package com.greenknightlabs.scp_001.posts.fragments.post_fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnLayout
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.extensions.askConfirmation
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.extensions.screenWidth
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.databinding.FragmentPostBinding
import com.greenknightlabs.scp_001.posts.models.Post
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PostFragment : BaseFragment<FragmentPostBinding>(R.layout.fragment_post) {
    // properties
    private val vm: PostFragmentViewModel by viewModels()
    var post: Post? = null

    // functions
    override fun activityTitle(): String {
        return vm.post.value?.title ?: "Post"
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm
        if (vm.post.value == null) {
            vm.post.value = post
        }

        vm.post.value?.user?.user?.avatarUrl?.let {
            binding.fragmentPostComponentPostAuthor.componentPostAuthorImageView.load(it) {
                size(140)
                transformations(CircleCropTransformation())
                crossfade(true)
                error(R.drawable.default_avatar)
            }
        }
        vm.post.value?.media?.let { media ->
            val screenWidth = activity?.screenWidth() ?: 0
            binding.fragmentPostImageView.layoutParams.height = media.calculateHeight(screenWidth, false)

            binding.fragmentPostImageView.load(media.url) {
                size(360)
                crossfade(true)
                error(R.drawable.ic_cancel)
            }
        }

        vm.toastMessage.observe(viewLifecycleOwner) {
            if (it != null) {
                activity?.makeToast(it)
                vm.toastMessage.value = null
            }
        }
        vm.shouldShowConfirmAlert.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.shouldShowConfirmAlert.value = false
                activity?.askConfirmation { vm.confirmAlertAction.value?.invoke() }
            }
        }
    }
}
