package com.virser.image_library.cache

import android.graphics.Bitmap
import android.os.SystemClock
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

internal class InMemoryTtlCache @Inject constructor(
    private val timeToLiveMillis: Long,
    private val cacheSizeItems: Int,
) : BitmapCache {

    private val cacheEntries = ConcurrentHashMap<String, BitmapCacheData>()

    private val size: Int
        get() = cacheEntries.size

    private val isCacheFull: Boolean
        get() = size >= cacheSizeItems

    override fun add(url: String, bitmap: Bitmap) {
        if (isCacheFull) {
            removeOldest()
        }
        cacheEntries[url] = BitmapCacheData(
            bitmap = bitmap, timeWhenExpire = SystemClock.elapsedRealtime() + timeToLiveMillis
        )
    }

    override fun get(url: String): Bitmap? = cacheEntries[url]?.let {
        if (it.timeWhenExpire < SystemClock.elapsedRealtime()) {
            remove(url)
            null
        } else {
            it.bitmap
        }
    }

    override fun invalidate() {
        cacheEntries.clear()
    }

    override fun removeExpired() {
        val currentElapsedTime = SystemClock.elapsedRealtime()
        cacheEntries.entries.removeIf { it.value.timeWhenExpire < currentElapsedTime }
    }

    private fun remove(key: String) {
        cacheEntries.remove(key)
    }

    private fun removeOldest() {
        val oldestEntry = cacheEntries.reduceEntries(1) { reduced, current ->
            if (reduced.value.timeWhenExpire < current.value.timeWhenExpire) {
                reduced
            } else {
                current
            }
        }
        oldestEntry?.let {
            cacheEntries.remove(it.key)
        }
    }
}
