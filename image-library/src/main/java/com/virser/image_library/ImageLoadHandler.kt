package com.virser.image_library

import android.content.Context
import android.content.ContextWrapper
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.virser.image_library.cache.BitmapCache
import com.virser.image_library.di.ImageLoaderModule
import com.virser.image_library.loader.BitmapLoader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import java.util.Collections
import java.util.WeakHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ImageLoadHandler @Inject constructor(
    @ImageLoaderModule.IO private val dispatcherIO: CoroutineDispatcher,
    @ImageLoaderModule.MainImmediate private val dispatcherMain: CoroutineDispatcher,
    private val cache: BitmapCache,
    private val loader: BitmapLoader,
) {

    private val jobMap: MutableMap<ImageView, Job> = Collections.synchronizedMap(WeakHashMap())

    fun startImageLoadingJob(
        context: Context,
        url: String,
        imageView: ImageView,
        @DrawableRes placeHolder: Int? = null,
        @DrawableRes errorHolder: Int? = null,
    ) {
        var localContext = context
        if (context is ContextWrapper) {
            localContext = context.baseContext
        }

        if ((localContext is LifecycleOwner).not()) {
            throw IllegalArgumentException("Context must be one of Activity or Fragment")
        }
        jobMap[imageView]?.cancel()

        jobMap[imageView] = (localContext as LifecycleOwner).lifecycleScope.launch {
            placeHolder?.let { imageView.setImageResource(placeHolder) }

            withContext(dispatcherIO) {
                cache.get(url)?.let { cachedBitmap ->
                    withContext(dispatcherMain) {
                        imageView.setImageBitmap(cachedBitmap)
                    }
                } ?: run {
                    loader.loadBitmap(url, { imageView.width }, { imageView.height })
                        ?.let { loadedBitmap ->
                            cache.add(url, loadedBitmap)
                            withContext(dispatcherMain) {
                                imageView.setImageBitmap(loadedBitmap)
                            }
                        } ?: run {
                        withContext(dispatcherMain) {
                            errorHolder?.let { imageView.setImageResource(it) }
                        }
                    }
                }
            }
        }
    }
}
