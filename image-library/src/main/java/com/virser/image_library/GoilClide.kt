package com.virser.image_library

import android.app.Activity
import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.virser.image_library.cache.BitmapCache
import com.virser.image_library.cache.CacheDaemon
import com.virser.image_library.di.Injector
import javax.inject.Inject

class GoilClide {

    @Inject
    internal lateinit var jobHandler: ImageLoadHandler

    @Inject
    internal lateinit var cache: BitmapCache

    @Inject
    internal lateinit var cacheClearingDaemon: CacheDaemon

    fun load(
        context: Context,
        url: String,
        imageView: ImageView,
        @DrawableRes placeHolder: Int? = null,
        @DrawableRes errorHolder: Int? = null,
    ) {
        jobHandler.startImageLoadingJob(context, url, imageView, placeHolder, errorHolder)
    }

    fun load(
        context: Fragment,
        url: String,
        imageView: ImageView,
        @DrawableRes placeHolder: Int? = null,
        @DrawableRes errorHolder: Int? = null,
    ) {
        jobHandler.startImageLoadingJob(
            context.requireContext(),
            url,
            imageView,
            placeHolder,
            errorHolder
        )
    }

    fun load(
        context: Activity,
        url: String,
        imageView: ImageView,
        @DrawableRes placeHolder: Int? = null,
        @DrawableRes errorHolder: Int? = null,
    ) {
        jobHandler.startImageLoadingJob(context, url, imageView, placeHolder, errorHolder)
    }

    fun load(
        context: FragmentActivity,
        url: String,
        imageView: ImageView,
        @DrawableRes placeHolder: Int? = null,
        @DrawableRes errorHolder: Int? = null,
    ) {
        jobHandler.startImageLoadingJob(context, url, imageView, placeHolder, errorHolder)
    }

    fun invalidateCache() {
        cache.invalidate()
    }

    companion object {
        private var instance: GoilClide? = null

        @JvmStatic
        @Synchronized
        fun init() {
            if (instance == null) {
                instance = GoilClide().also { loader ->
                    Injector.init(loader)
                    loader.cacheClearingDaemon.init()
                }
            }
        }

        @JvmStatic
        @Synchronized
        fun get(): GoilClide = instance ?: throw IllegalAccessException(
            "ImageLoader is not initialized, init() should be called before" +
                    " requesting an instance"
        )
    }

}
