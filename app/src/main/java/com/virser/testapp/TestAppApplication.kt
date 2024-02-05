package com.virser.testapp

import android.app.Application
import com.virser.image_library.GoilClide
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TestAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GoilClide.init()
    }
}
