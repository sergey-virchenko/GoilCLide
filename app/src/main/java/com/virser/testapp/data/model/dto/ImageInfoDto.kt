package com.virser.testapp.data.model.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageInfoDto(
    @Json(name = "id")
    val id: Int,
    @Json(name = "imageUrl")
    val imageUrl: String,
)
