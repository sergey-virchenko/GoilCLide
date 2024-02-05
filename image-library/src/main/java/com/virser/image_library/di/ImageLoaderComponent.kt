package com.virser.image_library.di

import com.virser.image_library.GoilClide
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ImageLoaderModule::class])
internal interface ImageLoaderComponent {
    fun inject(goilClide: GoilClide)
}
