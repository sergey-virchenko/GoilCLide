package com.virser.testapp.data.api

import com.virser.testapp.data.model.dto.ImageInfoDto
import retrofit2.Response
import retrofit2.http.GET

interface ZippoAppsApi {

    @GET("image_list.json")
    suspend fun getImageList(): Response<List<ImageInfoDto>>
}
