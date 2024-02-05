package com.virser.testapp.data.images

import com.virser.testapp.core.model.ImageInfo
import com.virser.testapp.data.ApiResult
import kotlinx.coroutines.flow.Flow

interface ImagesRepository {
    suspend fun getImages(): Flow<ApiResult<List<ImageInfo>>>
}
