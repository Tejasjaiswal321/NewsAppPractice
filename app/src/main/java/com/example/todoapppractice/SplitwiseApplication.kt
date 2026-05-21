package com.example.todoapppractice

import android.app.Application
import com.example.todoapppractice.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SplitwiseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            modules(appModule)
        }
    }
}