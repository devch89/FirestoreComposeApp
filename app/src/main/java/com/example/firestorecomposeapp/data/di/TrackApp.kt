package com.example.firestorecomposeapp.data.di

import android.app.Application
import com.example.firestorecomposeapp.App
import com.example.firestorecomposeapp.firestore.FirestoreRepositoryImpl
import com.example.injection_sdk.Injection
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TrackApp: Application() {

    override fun onCreate() {
        super.onCreate()


    }
}