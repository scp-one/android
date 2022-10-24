package com.greenknightlabs.scp_001.media.fragments.media_collection_fragment

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greenknightlabs.scp_001.app.config.AppConstants
import com.greenknightlabs.scp_001.app.enums.PageState
import com.greenknightlabs.scp_001.app.util.NavMan
import com.greenknightlabs.scp_001.app.view_models.PageViewModel
import com.greenknightlabs.scp_001.auth.util.AuthMan
import com.greenknightlabs.scp_001.media.MediaService
import com.greenknightlabs.scp_001.media.config.MediaConstants
import com.greenknightlabs.scp_001.media.dtos.GetMediaFilterDto
import com.greenknightlabs.scp_001.media.enums.MediaSortField
import com.greenknightlabs.scp_001.media.enums.MediaSortOrder
import com.greenknightlabs.scp_001.media.fragments.media_collection_fragment.adapters.MediaCollectionFragmentAdapter
import com.greenknightlabs.scp_001.media.models.Media
import dagger.hilt.android.lifecycle.HiltViewModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MediaCollectionFragmentViewModel @Inject constructor(
    private val mediaService: MediaService,
    private val authMan: AuthMan,
    private val navMan: NavMan,
    private val json: Json
) : PageViewModel<Media>() {
    // properties
    var adapter: MediaCollectionFragmentAdapter? = null

    val sortField = MutableLiveData(MediaSortField.CreatedAt)
    val sortOrder = MutableLiveData(MediaSortOrder.Descending)

    val selectedMedia = MutableLiveData<Media?>(null)
    var selectedMediaPosition = -1

    var listener: MediaCollectionFragment.Listener? = null

    // init
    init {
        paginate(true)
    }

    // functions
    override fun handleOnTapFailToLoad() {
        if (state.value != PageState.Fetching) {
            paginate(true)
        }
    }

    override fun paginate(refresh: Boolean) {
        state.value = PageState.Fetching

        viewModelScope.launch {
            val dto = provideFilterDto(refresh)

            try {
                val media = mediaService.getMedia(dto)

                if (refresh) {
                    items.value?.clear()
                    items.value?.addAll(media)
                    adapter?.notifyDataSetChanged()
                } else if (media.isNotEmpty()) {
                    items.value?.addAll(media)
                    adapter?.notifyItemInserted(items.value!!.size)
                }

                state.value = when (media.size < (dto.limit ?: MediaConstants.MEDIA_PAGE_SIZE)) {
                    true -> PageState.Bottom
                    else -> PageState.Idle
                }
                failedToLoad.value = false
            } catch (e: Throwable) {
                Timber.e(e)
                failedToLoad.value = true
                state.value = PageState.Bottom
                toastMessage.value = e.message
            }
        }
    }

    private fun provideFilterDto(refresh: Boolean): GetMediaFilterDto {
        var cursor: String? = null
        items.value?.lastOrNull()?.let { lastMedia ->
            try {
                val map = json.encodeToJsonElement(lastMedia).jsonObject.toMap()
                map[sortField.value?.rawValue]?.let { fieldValue ->
                    cursor = fieldValue.toString().replace("\"", "")
                }
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }

        val sort = "${sortField.value?.rawValue},${sortOrder.value?.rawValue}"

        return GetMediaFilterDto(
            null,
            authMan.payload?.id,
            sort,
            if (refresh) null else cursor,
            MediaConstants.MEDIA_PAGE_SIZE
        )
    }

    fun uploadImage(context: Context, file: File) {
        val originalState = state.value
        state.value = PageState.Fetching

        viewModelScope.launch {
            val compressedFile = Compressor.compress(context, file) {
                resolution(AppConstants.IMAGE_MAX_WIDTH, AppConstants.IMAGE_MAX_WIDTH)
                quality(80)
                format(Bitmap.CompressFormat.JPEG)
                size(1_048_576) // 1 MB
            }

            val fileSize = compressedFile.length() / 1024

            if (fileSize > 1000) {
                state.value = originalState
                toastMessage.value = "Image is too large."
            } else {
                try {
                    val media = mediaService.uploadMedia(compressedFile)

                    state.value = originalState
                    items.value?.add(0, media)

                    adapter?.notifyItemInserted(0)
                } catch (e: Throwable) {
                    state.value = originalState
                    toastMessage.value = e.message
                }
            }
        }
    }

    fun deleteMedia() {
        val media = selectedMedia.value ?: return
        val position = selectedMediaPosition

        val originalState = state.value
        state.value = PageState.Fetching

        viewModelScope.launch {
            try {
                mediaService.deleteMedia(media.id)

                items.value?.removeAt(position)

                state.value = originalState
                adapter?.notifyItemRemoved(position)
                clearMediaSelection()
            } catch (e: Throwable) {
                state.value = originalState
                toastMessage.value = e.message
            }
        }
    }

    fun handleOnTapMedia(position: Int) {
        val media = items.value!![position]

        if (selectedMedia.value != null && selectedMedia.value!!.id == media.id) {
            clearMediaSelection()
        } else {
            selectedMediaPosition = position
            selectedMedia.value = media
        }
    }

    fun clearMediaSelection() {
        selectedMediaPosition = -1
        selectedMedia.value = null
    }
}
