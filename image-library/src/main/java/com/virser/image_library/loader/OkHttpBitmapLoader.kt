package com.virser.image_library.loader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import javax.inject.Inject

internal class OkHttpBitmapLoader @Inject constructor(
    private val httpClient: OkHttpClient,
) : BitmapLoader {

    override suspend fun loadBitmap(
        url: String,
        targetWidth: () -> Int,
        targetHeight: () -> Int,
    ): Bitmap? {
        return getImageBitmap(url, targetWidth, targetHeight)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getImageBitmap(
        url: String,
        targetWidth: () -> Int,
        targetHeight: () -> Int,
    ): Bitmap? = suspendCancellableCoroutine { continuation ->
        val okHttpCallback = object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let {
                        readBitmap(it, targetWidth, targetHeight)
                    }
                } else {
                    Log.d("TAGGG", "error for $url")
                    null
                }.let {
                    continuation.resume(it, null)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                if (continuation.isCancelled) return
                Log.d("TAGGG", "failure for $url")
                continuation.resume(null, null)
            }
        }

        httpClient.newCall(
            Request.Builder().url(url).tag(url).build()
        ).enqueue(okHttpCallback)

        continuation.invokeOnCancellation {
            httpClient.dispatcher.apply {
                queuedCalls().firstOrNull { it.request().tag() == url }?.cancel()
                runningCalls().firstOrNull { it.request().tag() == url }?.cancel()
            }
        }
    }

    private fun readBitmap(
        body: ResponseBody,
        targetWidth: () -> Int,
        targetHeight: () -> Int,
    ): Bitmap? = BitmapFactory.Options().run {
        inJustDecodeBounds = true
        val bytesArray = body.bytes()
        BitmapFactory.decodeByteArray(
            bytesArray, 0, bytesArray.size, this
        )
        runCatching {
            inSampleSize = calculateInSampleSize(this, targetWidth, targetHeight)
            inJustDecodeBounds = false
            BitmapFactory.decodeByteArray(bytesArray, 0, bytesArray.size, this)
        }.getOrNull()
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        targetWidthFunc: () -> Int,
        targetHeightFunc: () -> Int,
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        var targetWidth: Int
        var targetHeight: Int

        runBlocking {
            targetWidth = targetWidthFunc()
            targetHeight = targetHeightFunc()
            if (targetWidth == 0 && targetHeight == 0) {
                val timeForUpdate = SystemClock.elapsedRealtime() + 500
                //stall half a second in hope to get
                while (timeForUpdate > SystemClock.elapsedRealtime()){
                    targetWidth = targetWidthFunc()
                    targetHeight = targetHeightFunc()
                    if (targetWidth != 0 || targetHeight != 0){
                        break
                    }
                }
            }
        }
        if (targetWidth == 0 && targetHeight == 0){
            throw IllegalArgumentException()
        }

        if (height > targetHeight || width > targetWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (keepIterations(
                    halfWidth, halfHeight, targetWidth, targetHeight, inSampleSize
                )
            ) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun keepIterations(
        halfWidth: Int,
        halfHeight: Int,
        targetWidth: Int,
        targetHeight: Int,
        inSampleSize: Int,
    ): Boolean {
        val height = (halfHeight / inSampleSize) >= targetHeight
        val width = (halfWidth / inSampleSize) >= targetWidth
        return height && width
    }

}
