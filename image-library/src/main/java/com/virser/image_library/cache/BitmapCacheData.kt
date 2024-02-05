package com.virser.image_library.cache

import android.graphics.Bitmap

internal data class BitmapCacheData(
    val bitmap: Bitmap,
    val timeWhenExpire: Long,
)
