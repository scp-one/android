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
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.greenknightlabs.scp_001.R
import com.greenknightlabs.scp_001.app.activities.MainActivity
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.extensions.makeToast
import com.greenknightlabs.scp_001.app.fragments.base_fragment.BaseFragment
import com.greenknightlabs.scp_001.app.util.Kairos
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.databinding.FragmentMediaCollectionBinding
import com.greenknightlabs.scp_001.media.fragments.media_collection_fragment.adapters.MediaCollectionFragmentAdapter
import com.greenknightlabs.scp_001.media.models.Media
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import javax.inject.Inject

@AndroidEntryPoint
class MediaCollectionFragment : BaseFragment<FragmentMediaCollectionBinding>(R.layout.fragment_media_collection) {
    interface Listener {
        fun handleMediaSelected(media: Media)
    }

    // dependencies
    @Inject lateinit var navMan: NavMan
    @Inject lateinit var kairos: Kairos

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

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_fragment_media_collection_delete -> handleOnTapMenuDelete()
            R.id.menu_fragment_media_collection_add -> handleOnTapMenuAdd()
            R.id.menu_fragment_media_collection_select -> handleOnTapMenuSelect()
        }
        return false
    }

    private fun handleOnTapMenuDelete() {
        vm.deleteMedia()
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

        val adapter = MediaCollectionFragmentAdapter(vm, kairos)
        val layoutManager = GridLayoutManager(activity, 3, RecyclerView.VERTICAL, false)

        binding.fragmentMediaCollectionRecyclerView.adapter = adapter
        binding.fragmentMediaCollectionRecyclerView.layoutManager = layoutManager

        vm.state.observe(viewLifecycleOwner) {
            (activity as? MainActivity)?.showProgressBar(it == PageState.Fetching)
        }
        vm.toastMessage.observe(viewLifecycleOwner) {
            activity?.makeToast(it)
        }
        vm.didRefresh.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.didRefresh.value = false
                adapter.notifyDataSetChanged()
            }
        }
        vm.didInsertBefore.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.didInsertBefore.value = false
                adapter.notifyItemInserted(0)
            }
        }
        vm.didInsertAfter.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.didInsertAfter.value = false
                adapter.notifyItemInserted(vm.items.value!!.size)
            }
        }
        vm.didDelete.observe(viewLifecycleOwner) {
            if (it == true) {
                vm.didDelete.value = false
                adapter.notifyItemRemoved(vm.selectedMediaPosition)
                vm.clearMediaSelection()
            }
        }
        vm.selectedMedia.observe(viewLifecycleOwner) {
            adapter.handleSelectedMediaChanged(vm.selectedMediaPosition)
            setMenuItemsVisibility()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateMenu(menu, menuInflater)
        setMenuItemsVisibility()
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

        startForResult.launch(intent)
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            try {
                result.data?.data?.let {
                    val file = fileFromContentUri(requireContext(), it)
                    vm.uploadImage(file)
                }
            } catch (e: Throwable) {
                vm.toastMessage.value = e.message
            }
        }
    }

    fun fileFromContentUri(context: Context, contentUri: Uri): File {
        // Preparing Temp file name
        val fileExtension = getFileExtension(context, contentUri)
        val fileName = "temp_file" + if (fileExtension != null) ".$fileExtension" else ""

        // Creating Temp file
        val tempFile = File(context.cacheDir, fileName)
        tempFile.createNewFile()

        try {
            val oStream = FileOutputStream(tempFile)
            val inputStream = context.contentResolver.openInputStream(contentUri)

            inputStream?.let {
                copy(inputStream, oStream)
            }

            oStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return tempFile
    }

    private fun getFileExtension(context: Context, uri: Uri): String? {
        val fileType: String? = context.contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
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