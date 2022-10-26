package com.greenknightlabs.scp_001.media.fragments.media_collection_fragment

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.askConfirmation
import com.greenknightlabs.scp_001.app.extensions.getFileExtension
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.databinding.FragmentMediaCollectionBinding
import com.greenknightlabs.scp_001.media.fragments.media_collection_fragment.adapters.MediaCollectionFragmentAdapter
import com.greenknightlabs.scp_001.media.models.Media
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import java.io.*
import javax.inject.Inject

@AndroidEntryPoint
class MediaCollectionFragment : BaseFragment<FragmentMediaCollectionBinding>(R.layout.fragment_media_collection) {
    // interfaces
    interface Listener {
        fun handleMediaSelected(media: Media)
    }

    // dependencies
    @Inject lateinit var navMan: NavMan

    // properties
    private val vm: MediaCollectionFragmentViewModel by viewModels()
    var listener: Listener? = null

    // functions
    override fun activityTitle(): String {
        return "Media"
    }

    override fun menuId(): Int? {
        return R.menu.menu_fragment_media_collection
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateMenu(menu, menuInflater)
        setMenuItemsVisibility()
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_fragment_media_collection_delete -> handleOnTapMenuDelete()
            R.id.menu_fragment_media_collection_add -> handleOnTapMenuAdd()
            R.id.menu_fragment_media_collection_select -> handleOnTapMenuSelect()
        }
        return false
    }

    private fun handleOnTapMenuDelete() {
        vm.handleOnTapDelete()
    }

    private fun handleOnTapMenuAdd() {
        openActivityForResult()
    }

    private fun handleOnTapMenuSelect() {
        val selectedMedia = vm.selectedMedia.value ?: return
        vm.listener?.handleMediaSelected(selectedMedia)
        navMan.popFragment()
    }

    override fun configureView(view: View, savedInstanceState: Bundle?) {
        super.configureView(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        if (vm.listener == null) {
            vm.listener = listener
        }
        if (vm.adapter == null) {
            vm.adapter = MediaCollectionFragmentAdapter(vm)
        }

        val layoutManager = GridLayoutManager(activity, 3, RecyclerView.VERTICAL, false)

        binding.fragmentMediaCollectionRecyclerView.adapter = vm.adapter!!
        binding.fragmentMediaCollectionRecyclerView.layoutManager = layoutManager
        binding.fragmentMediaCollectionRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (vm.state.value != PageState.Idle) return
                if (layoutManager.findLastVisibleItemPosition() != (vm.items.value?.size ?: 0) - 1) return
                vm.paginate(false)
            }
        })

        vm.state.observe(viewLifecycleOwner) {
            (activity as? MainActivity)?.showProgressBar(it == PageState.Fetching)
        }
        vm.toastMessage.observe(viewLifecycleOwner) {
            if (it != null) {
                activity?.makeToast(it)
                vm.toastMessage.value = null
            }
        }
        vm.shouldShowConfirmAlert.observe(viewLifecycleOwner) {
            if (it == true) {
                activity?.askConfirmation { vm.confirmAlertAction.value?.invoke() }
                vm.shouldShowConfirmAlert.value = false
            }
        }
        vm.selectedMedia.observe(viewLifecycleOwner) {
            vm.adapter?.handleSelectedMediaChanged(vm.selectedMediaPosition)
            setMenuItemsVisibility()
        }
    }

    private fun setMenuItemsVisibility() {
        val menuItemDelete = menu?.getItem(0)
        val menuItemAdd = menu?.getItem(1)
        val menuItemSelect = menu?.getItem(2)

        menuItemDelete?.isVisible = vm.selectedMedia.value != null
        menuItemAdd?.isVisible = vm.selectedMedia.value == null
        menuItemSelect?.isVisible = vm.selectedMedia.value != null && vm.listener != null
    }

    private fun openActivityForResult() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        registerForActivityResult.launch(intent)
    }

    private val registerForActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            try {
                result.data?.data?.let {
                    val file = createTempFileFromUri(requireContext(), it)
                    vm.uploadImage(requireContext(), file)
                }
            } catch (e: Throwable) {
                vm.toastMessage.value = e.message
            }
        }
    }

    private fun createTempFileFromUri(context: Context, uri: Uri): File {
        // Preparing Temp file name
        val fileExtension = uri.getFileExtension(context)
        val fileName = "temp_file" + if (fileExtension != null) ".$fileExtension" else ""

        // Creating Temp file
        val tempFile = File(context.cacheDir, fileName)
        tempFile.createNewFile()

        try {
            val outputStream = FileOutputStream(tempFile)
            val inputStream = context.contentResolver.openInputStream(uri)

            inputStream?.let {
                copy(inputStream, outputStream)
            }

            outputStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return tempFile
    }

    @Throws(IOException::class)
    private fun copy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }
}
