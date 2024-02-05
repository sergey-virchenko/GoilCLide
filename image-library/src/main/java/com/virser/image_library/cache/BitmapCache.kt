package com.virser.image_library.cache

import android.graphics.Bitmap

internal interface BitmapCache {
    fun get(url: String): Bitmap?
    fun add(url: String, bitmap: Bitmap)
    fun removeExpired()
    fun invalidate()
}
