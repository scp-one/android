package com.greenknightlabs.scp_001.app.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.collection.LruCache
import com.greenknightlabs.scp_001.app.enums.MemTrimLevel
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

//@Singleton
//class Kairos @Inject constructor(
//    private val stash: Stash
//) {
//    // internal properties
//    private var memCache: LruCache<String, Bitmap>
//    private val pendingUrls = mutableSetOf<String>()
//    private val errorUrls = mutableSetOf<String>()
//    private val pendingRequests = mutableMapOf<String, MutableList<() -> Unit>>()
//
//    // init
//    init {
//        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8
//        memCache = object : LruCache<String, Bitmap>(maxMemory) {
//            override fun sizeOf(key: String, bitmap: Bitmap): Int {
//                return bitmap.byteCount / 1024
//            }
//        }
//    }
//
//    // main function
//    fun load(url: String?): KairosRequestBuilder {
//        return KairosRequestBuilder(this, url)
//    }
//
//    // request builder
//    class KairosRequestBuilder(private val kairos: Kairos, val url: String?) {
//        var maxWidth: Int = 720
//            private set
//        var maxHeight: Int = 720
//            private set
//        var save: Boolean = true
//            private set
//        var defaultImageResId: Int? = null
//            private set
//        lateinit var imageView: ImageView
//            private set
//
//        fun scale(width: Int, height: Int) = apply {
//            this.maxWidth = width
//            this.maxHeight = height
//        }
//        fun forget() = apply { this.save = false }
//        fun default(resId: Int) = apply { this.defaultImageResId = resId }
//
//        // main function
//        fun into(imageView: ImageView) {
//            this.imageView = imageView
//            GlobalScope.launch(Dispatchers.Main) {
//                kairos.loadRequest(this@KairosRequestBuilder)
//            }
//        }
//    }
//
//    // local functions
//    private suspend inline fun loadRequest(request: KairosRequestBuilder) {
//        request.imageView.tag = request.url
//        request.imageView.alpha = 0f
//
//        val result = get(request)
//        if (request.imageView.tag != request.url) return
//
//        when (result.isFailure) {
//            true -> request.defaultImageResId?.let { request.imageView.setImageResource(it) }
//            else -> request.imageView.setImageBitmap(result.getOrNull())
//        }
//
//        request.imageView.animate().alpha(1f).duration = 250
//    }
//
//    private suspend inline fun get(request: KairosRequestBuilder): Result<Bitmap> {
//        if (request.url == null) {
//            return Result.failure(Throwable("No URL provided."))
//        }
//
//        if (errorUrls.contains(request.url)) {
//            return Result.failure(Throwable("Error encountered at URL."))
//        }
//
//        memCache[request.url]?.let {
//            return Result.success(it)
//        }
//
//        if (pendingUrls.contains(request.url)) {
////            appendRequest(request)
//            return Result.failure(Throwable("Request already pending."))
//        }
//        pendingUrls.add(request.url)
//
//        val imageFromDisk = getFromDisk(request).getOrNull()
//        if (imageFromDisk != null) {
//            cache(imageFromDisk, request.url, false)
////            loadPendingRequests(request.url)
//            pendingUrls.remove(request.url)
//            return Result.success(imageFromDisk)
//        }
//
//        val imageFromNetwork = getFromNetwork(request).getOrNull()
//        if (imageFromNetwork != null) {
//            cache(imageFromNetwork, request.url, true)
////            loadPendingRequests(request.url)
//            pendingUrls.remove(request.url)
//            return Result.success(imageFromNetwork)
//        }
//
//        errorUrls.add(request.url)
//        return Result.failure(Throwable("Bitmap not found."))
//    }
//
////    private fun appendRequest(request: KairosRequestBuilder) {
////        if (pendingRequests[request.url] == null) {
////            pendingRequests[request.url ?: ""] = mutableListOf()
////        }
////        pendingRequests[request.url]?.add { runBlocking { loadRequest(request) } }
////    }
////
////    private fun loadPendingRequests(url: String) {
////        pendingRequests[url]?.forEach { it() }
////        pendingRequests.remove(url)
////    }
//
//    private suspend inline fun getFromDisk(request: KairosRequestBuilder): Result<Bitmap> = withContext(Dispatchers.IO) {
//        when (val data = stash.open(request.url ?: "").getOrNull()) {
//            null -> Result.failure(Throwable("Data not found on disk."))
//            else -> when (val bitmap = decodeBitmapFromByteArray(data, request).getOrNull()) {
//                null -> Result.failure(Throwable("Unable to decode bitmap."))
//                else -> Result.success(bitmap)
//            }
//        }
//    }
//
//    private suspend inline fun getFromNetwork(request: KairosRequestBuilder): Result<Bitmap> = withContext(Dispatchers.IO) {
//        try {
//            // TODO: figure out why this is an inappropriate call
//            val stream = URL(request.url).openStream()
//            when (val bitmap = BitmapFactory.decodeStream(stream)) {
//                null -> Result.failure(Throwable("Unable to decode bitmap."))
//                else -> Result.success(bitmap)
//            }
//        } catch (e: Throwable) {
//            Timber.e(e)
//            Result.failure(e)
//        }
//    }
//
//    private suspend inline fun decodeBitmapFromByteArray(data: ByteArray, request: KairosRequestBuilder): Result<Bitmap> = withContext(Dispatchers.Default) {
//        val bitmap = BitmapFactory.Options().run {
//            inJustDecodeBounds = true
//            BitmapFactory.decodeByteArray(data, 0, data.size, this)
//            inSampleSize = calculateInSampleSize(this, request.maxWidth, request.maxHeight)
//            inJustDecodeBounds = false
//            BitmapFactory.decodeByteArray(data, 0, data.size, this)
//        }
//
//        Result.success(bitmap)
//    }
//
//    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
//        // Raw height and width of image
//        val (height: Int, width: Int) = options.run { outHeight to outWidth }
//        var inSampleSize = 1
//
//        if (height > reqHeight || width > reqWidth) {
//            val halfHeight: Int = height / 2
//            val halfWidth: Int = width / 2
//
//            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//            // height and width larger than the requested height and width.
//            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
//                inSampleSize *= 2
//            }
//        }
//
//        return inSampleSize
//    }
//
//    private suspend inline fun cache(bitmap: Bitmap, url: String, saveToStash: Boolean) = withContext(Dispatchers.IO) {
//        memCache.put(url, bitmap)
//        if (saveToStash) {
//            val stream = ByteArrayOutputStream()
//            val compressionQuality = 80
//            bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, stream)
//            stash.put(stream.toByteArray(), url)
//        }
//    }
//
//    fun trimMemory(level: MemTrimLevel) {
//        when (level) {
//            MemTrimLevel.LOW -> {
//                // TODO: trim a small amount
//            }
//            MemTrimLevel.MEDIUM -> {
//                // TODO: trim a medium amount
//                memCache.evictAll()
//            }
//            MemTrimLevel.HIGH -> {
//                memCache.evictAll()
//            }
//            MemTrimLevel.FULLY -> {
//                memCache.evictAll()
//                errorUrls.clear()
//            }
//        }
//    }
//}
