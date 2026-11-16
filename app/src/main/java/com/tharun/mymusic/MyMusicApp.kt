package com.tharun.mymusic

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyMusicApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
