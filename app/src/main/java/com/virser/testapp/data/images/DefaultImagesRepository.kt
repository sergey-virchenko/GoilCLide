package com.virser.testapp.data.images

import com.virser.testapp.core.model.ImageInfo
import com.virser.testapp.data.ApiResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultImagesRepository @Inject constructor(
    private val imagesRemoteDataSource: ImagesRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : ImagesRepository {

    override suspend fun getImages(): Flow<ApiResult<List<ImageInfo>>> = withContext(ioDispatcher) {
        flow {
            emit(imagesRemoteDataSource.getImages())
        }
    }
}
