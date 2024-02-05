package com.virser.image_library.di

import com.virser.image_library.GoilClide

internal object Injector {

    private lateinit var imageLoaderComponent: ImageLoaderComponent

    fun init(goilClide: GoilClide) {
        imageLoaderComponent = DaggerImageLoaderComponent
            .builder()
            .build()
        imageLoaderComponent.inject(goilClide)
    }
}
