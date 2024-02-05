package com.virser.image_library.di

import com.virser.image_library.cache.BitmapCache
import com.virser.image_library.loader.BitmapLoader
import com.virser.image_library.cache.InMemoryTtlCache
import com.virser.image_library.loader.OkHttpBitmapLoader
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
internal class ImageLoaderModule {
    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class IO

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class MainImmediate

    @Singleton
    @Provides
    fun provideBitmapCache(): BitmapCache = InMemoryTtlCache(CACHE_TTL, CACHE_SIZE_ITEMS)

    @IO
    @Singleton
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainImmediate
    @Singleton
    @Provides
    fun provideMainImmediateDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate

    @Singleton
    @Provides
    fun provideDaemonScope(): CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Singleton
    @Provides
    fun provideOkHttpBitmapLoader(okHttpClient: OkHttpClient): BitmapLoader =
        OkHttpBitmapLoader(okHttpClient)

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().readTimeout(TIMEOUT_DEFAULT, TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT_DEFAULT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_DEFAULT, TimeUnit.SECONDS)
            .callTimeout(TIMEOUT_DEFAULT, TimeUnit.SECONDS).build()

    companion object {
        const val TIMEOUT_DEFAULT = 30L
        const val CACHE_TTL = 4 * 60 * 60 * 1000L//4 hours
        const val CACHE_SIZE_ITEMS = 50
    }
}
