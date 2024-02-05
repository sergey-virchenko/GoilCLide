package com.virser.testapp.data.images

import com.virser.testapp.core.model.ImageInfo
import com.virser.testapp.data.ApiResult
import com.virser.testapp.data.api.ApiCallHandler
import com.virser.testapp.data.api.ZippoAppsApi
import com.virser.testapp.data.imageInfoDtoToImageInfo
import javax.inject.Inject

class DefaultImagesRemoteDataSource @Inject constructor(
    private val callHandler: ApiCallHandler,
    private val api: ZippoAppsApi,
) : ImagesRemoteDataSource {

    override suspend fun getImages(): ApiResult<List<ImageInfo>> = callHandler.makeCall(
        call = { api.getImageList() },
        mapper = { data -> data.map { imageInfoDtoToImageInfo(it) } }
    )
}
