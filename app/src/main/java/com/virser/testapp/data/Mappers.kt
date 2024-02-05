package com.virser.testapp.data

import com.virser.testapp.core.model.ImageInfo
import com.virser.testapp.data.model.dto.ImageInfoDto

fun imageInfoDtoToImageInfo(imageInfoDto: ImageInfoDto): ImageInfo =
    ImageInfo(imageInfoDto.id, imageInfoDto.imageUrl)
