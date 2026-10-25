package com.dutradev.carimbosocian

import android.app.Application
import com.dutradev.carimbosocian.di.AppContainer
import com.dutradev.carimbosocian.di.DefaultAppContainer

class CarimboxApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
