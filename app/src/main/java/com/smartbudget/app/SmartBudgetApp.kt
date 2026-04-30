package com.smartbudget.app

import android.app.Application
import com.smartbudget.app.util.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartBudgetApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
    }
}