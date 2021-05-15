package com.mirenzen.scp_001.app.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.collection.LruCache
import com.mirenzen.scp_001.app.enums.MemTrimLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Kairos @Inject constructor(
    private val stash: Stash
) {
    // internal properties
    private var memCache: LruCache<String, Bitmap>
    private val pendingUrls = mutableSetOf<String>()
    private val errorUrls = mutableSetOf<String>()
    private val pendingRequests = mutableMapOf<String, MutableList<() -> Unit>>()

    // init
    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8
        memCache = object : LruCache<String, Bitmap>(maxMemory) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
    }

    // request builder
    class KairosRequestBuilder(private val kairos: Kairos, val url: String?) {
        var maxWidth: Int = 720
            private set
        var maxHeight: Int = 720
            private set
        var save: Boolean = true
            private set
        var defaultImageResId: Int? = null
            private set
        lateinit var imageView: ImageView
            private set

        fun scale(width: Int, height: Int) = apply {
            this.maxWidth = width
            this.maxHeight = height
        }
        fun forget() = apply { this.save = false }
        fun default(resId: Int) = apply { this.defaultImageResId = resId }

        fun into(imageView: ImageView) {
            this.imageView = imageView
            kairos.loadRequest(this)
        }
    }

    // functions
    fun load(url: String?): KairosRequestBuilder {
        return KairosRequestBuilder(this, url)
    }

    private fun loadRequest(request: KairosRequestBuilder) {
        request.imageView.tag = request.url
        request.imageView.alpha = 0f
        runBlocking {
            val image = get(request).getOrNull()
            if (request.imageView.tag != request.url) { return@runBlocking }
            if (image == null) {
                val default = request.defaultImageResId
                if (default != null) {
                    request.imageView.setImageResource(default)
                }
            } else {
                request.imageView.setImageBitmap(image)
            }
            request.imageView.animate().alpha(1f).duration = 250
        }
    }

    private suspend fun get(request: KairosRequestBuilder): Result<Bitmap> = withContext(Dispatchers.IO) {
        if (request.url == null) {
            return@withContext Result.failure(Throwable("No URL provided."))
        }
        if (errorUrls.contains(request.url)) {
            return@withContext Result.failure(Throwable("Error encountered at URL."))
        }
        memCache[request.url]?.let {
            return@withContext Result.success(it)
        }
        if (pendingUrls.contains(request.url)) {
            appendRequest(request)
            return@withContext Result.failure(Throwable("Request already pending."))
        }
        pendingUrls.add(request.url)

        val imageFromDisk = getFromDisk(request).getOrNull()
        if (imageFromDisk != null) {
            cache(imageFromDisk, request.url, false)
            loadPendingRequests(request.url)
            pendingUrls.remove(request.url)
            return@withContext Result.success(imageFromDisk)
        }

        val imageFromNetwork = getFromNetwork(request).getOrNull()
        if (imageFromNetwork != null) {
            cache(imageFromNetwork, request.url, true)
            loadPendingRequests(request.url)
            pendingUrls.remove(request.url)
            return@withContext Result.success(imageFromNetwork)
        }

        errorUrls.add(request.url)
        return@withContext Result.failure(Throwable("Bitmap not found."))
    }

    private fun appendRequest(request: KairosRequestBuilder) {
        if (pendingRequests[request.url] == null) {
            pendingRequests[request.url ?: ""] = mutableListOf()
        }
        pendingRequests[request.url]?.add { loadRequest(request) }
    }

    private fun loadPendingRequests(url: String) {
        pendingRequests[url]?.forEach { it() }
        pendingRequests.remove(url)
    }

    private suspend fun getFromDisk(request: KairosRequestBuilder): Result<Bitmap> = withContext(Dispatchers.IO) {
        val data = stash.open(request.url ?: "").getOrNull()
        if (data == null) {
            return@withContext Result.failure(Throwable("Data not found on disk."))
        } else {
            val image = decodeBitmapFromByteArray(data, request).getOrNull()
            if (image == null) {
                return@withContext Result.failure(Throwable("Unable to decode bitmap."))
            } else {
                return@withContext Result.success(image)
            }
        }
    }

    private suspend fun getFromNetwork(request: KairosRequestBuilder): Result<Bitmap> = withContext(Dispatchers.IO) {
        try {
            // TODO: figure out why this is an inappropriate call
            val data = URL(request.url).readBytes()
            val image = decodeBitmapFromByteArray(data, request).getOrNull()
            if (image == null) {
                return@withContext Result.failure(Throwable("Unable to decode bitmap."))
            } else {
                return@withContext Result.success(image)
            }
        } catch (e: Throwable) {
            Timber.e(e)
            return@withContext Result.failure(e)
        }
    }

    private suspend fun decodeBitmapFromByteArray(data: ByteArray, request: KairosRequestBuilder): Result<Bitmap> = withContext(Dispatchers.Default) {
        val bitmap = BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(data, 0, data.size, this)
            inSampleSize = calculateInSampleSize(this, request.maxWidth, request.maxHeight)
            inJustDecodeBounds = false
            BitmapFactory.decodeByteArray(data, 0, data.size, this)
        }

        if (bitmap == null) {
            Result.failure(Throwable("Unable to decode bitmap."))
        } else {
            Result.success(bitmap)
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private suspend fun cache(image: Bitmap, url: String, saveToStash: Boolean) = withContext(Dispatchers.IO) {
        memCache.put(url, image)
        if (saveToStash) {
            val stream = ByteArrayOutputStream()
            val compressionQuality = 80
            image.compress(Bitmap.CompressFormat.JPEG, compressionQuality, stream)
            stash.put(stream.toByteArray(), url)
        }
    }

    fun trimMemory(level: MemTrimLevel) {
        when (level) {
            MemTrimLevel.LOW -> {
                // TODO: trim a small amount
            }
            MemTrimLevel.MEDIUM -> {
                // TODO: trim a medium amount
                memCache.evictAll()
            }
            MemTrimLevel.HIGH -> {
                memCache.evictAll()
            }
            MemTrimLevel.FULLY -> {
                memCache.evictAll()
                errorUrls.clear()
            }
        }
    }
}