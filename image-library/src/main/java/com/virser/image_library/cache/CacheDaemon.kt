package com.virser.image_library.cache

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CacheDaemon @Inject constructor(
    private val scope: CoroutineScope,
    private val cache: BitmapCache,
) {

    fun init() {
        scope.launch {
            while (true) {
                delay(CHECK_TIME_INTERVAL_MILLIS)
                cache.removeExpired()
            }
        }
    }

    companion object {
        const val CHECK_TIME_INTERVAL_MILLIS = 1000L//1 sec
    }
}
