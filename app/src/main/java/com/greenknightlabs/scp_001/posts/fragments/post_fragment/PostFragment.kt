package com.greenknightlabs.scp_001.posts.fragments.post_fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnLayout
import androidx.fragment.app.viewModels
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.extensions.askConfirmation
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.extensions.screenWidth
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.databinding.FragmentPostBinding
import com.greenknightlabs.scp_001.posts.models.Post
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PostFragment : BaseFragment<FragmentPostBinding>(R.layout.fragment_post) {
    // dendencies
    @Inject lateinit var kairos: Kairos

    // properties
    private val vm: PostFragmentViewModel by viewModels()
    var post: Post? = null

    // functions
    override fun activityTitle(): String {
        return post?.title ?: "Post"
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm
        if (vm.post.value == null) {
            vm.post.value = post
        }

        vm.post.value?.user?.user?.avatarUrl?.let {
            kairos.load(it).scale(140, 140).default(R.drawable.default_avatar).into(binding.fragmentPostComponentPostAuthor.componentPostAuthorImageView)
        }
        vm.post.value?.media?.let { media ->
            val screenWidth = activity?.screenWidth() ?: 0
            binding.fragmentPostImageView.layoutParams.height = media.calculateHeight(screenWidth, false)
            kairos.load(media.url).scale(360, 360).default(R.drawable.ic_face).into(binding.fragmentPostImageView)
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
