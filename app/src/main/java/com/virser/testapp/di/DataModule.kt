package com.virser.testapp.di

import com.virser.testapp.data.api.ApiCallHandler
import com.virser.testapp.data.api.ZippoAppsApi
import com.virser.testapp.data.images.DefaultImagesRemoteDataSource
import com.virser.testapp.data.images.DefaultImagesRepository
import com.virser.testapp.data.images.ImagesRemoteDataSource
import com.virser.testapp.data.images.ImagesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    fun provideImagesRepository(
        remoteDataSource: ImagesRemoteDataSource,
        ioDispatcher: CoroutineDispatcher,
    ): ImagesRepository = DefaultImagesRepository(
        remoteDataSource, ioDispatcher
    )

    @Provides
    fun provideImagesRemoteDataSource(
        apiCallHandler: ApiCallHandler,
        zippoApi: ZippoAppsApi,
    ): ImagesRemoteDataSource = DefaultImagesRemoteDataSource(apiCallHandler, zippoApi)

}
