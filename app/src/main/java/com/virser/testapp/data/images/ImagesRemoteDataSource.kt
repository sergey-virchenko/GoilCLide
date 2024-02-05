package com.virser.testapp.data.images

import com.virser.testapp.core.model.ImageInfo
import com.virser.testapp.data.ApiResult

interface ImagesRemoteDataSource {

    suspend fun getImages(): ApiResult<List<ImageInfo>>

}
