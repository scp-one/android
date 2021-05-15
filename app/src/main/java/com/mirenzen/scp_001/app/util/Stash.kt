package com.mirenzen.scp_001.app.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Stash @Inject constructor(
    context: Context
) {
    private val stashDir: File = context.cacheDir

    suspend fun put(data: ByteArray, dataId: String): Result<Nothing?> = withContext(Dispatchers.IO) {
        try {
            File(stashDir, formatDataId(dataId)).writeBytes(data)
            Result.success(null)
        } catch (e: Throwable) {
            Timber.e(e)
            Result.failure(e)
        }
    }

    suspend fun get(dataId: String): Result<File?> = withContext(Dispatchers.IO) {
        try {
            val file = File(stashDir, formatDataId(dataId))
            Result.success(file)
        } catch (e: Throwable) {
            Timber.e(e)
            Result.failure(e)
        }
    }

    suspend fun open(dataId: String): Result<ByteArray?> = withContext(Dispatchers.IO) {
        try {
            val file = File(stashDir, formatDataId(dataId))
            val data = file.readBytes()
            Result.success(data)
        } catch (e: Throwable) {
            Timber.e(e)
            Result.failure(e)
        }
    }

    suspend fun remove(dataId: String): Result<Nothing?> = withContext(Dispatchers.IO) {
        try {
            val file = File(stashDir, formatDataId(dataId))
            file.delete()
            Result.success(null)
        } catch (e: Throwable) {
            Timber.e(e)
            Result.failure(e)
        }
    }

    suspend fun empty(): Result<Nothing?> = withContext(Dispatchers.IO) {
        try {
            val files = stashDir.listFiles() ?: throw Error()
            for (file in files) {
                file.delete()
            }
            Result.success(null)
        } catch (e: Throwable) {
            Timber.e(e)
            Result.failure(e)
        }
    }

    private fun formatDataId(dataId: String): String {
        return dataId.replace("/", "-")
            .plus(".tmp").takeLast(80)
    }
}