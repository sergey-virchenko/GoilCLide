package com.virser.image_library.loader

import android.graphics.Bitmap

internal interface BitmapLoader {
    suspend fun loadBitmap(url: String, targetWidth: () -> Int, targetHeight: () -> Int): Bitmap?
}
