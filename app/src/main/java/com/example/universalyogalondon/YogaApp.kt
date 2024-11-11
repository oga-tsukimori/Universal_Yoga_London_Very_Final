package com.example.universalyogalondon

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class YogaApp : Application() {

    companion object {
        val TAG = YogaApp::class.java.simpleName
        private lateinit var instance: YogaApp

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        fun getInstance(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        instance = this
    }
}