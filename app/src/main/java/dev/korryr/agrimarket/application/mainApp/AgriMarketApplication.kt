package dev.korryr.agrimarket.application.mainApp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AgriMarketApplication : Application() {
//    override fun onCreate() {
//        super.onCreate()
//        FirebaseApp.initializeApp(this)
//        FirebaseAppCheck.getInstance()
//            .installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
//    }
}