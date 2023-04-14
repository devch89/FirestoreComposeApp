package com.example.firestorecomposeapp.data.di

import com.example.firestorecomposeapp.data.local.TasksDao
import com.example.firestorecomposeapp.data.repo.auth.AuthRepository
import com.example.firestorecomposeapp.data.repo.auth.AuthRepositoryImpl
import com.example.firestorecomposeapp.firestore.FirestoreRepository
import com.example.firestorecomposeapp.firestore.FirestoreRepositoryImpl
import com.example.firestorecomposeapp.firestore.location.LocationUseCase
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object Di {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesAuthRepositoryImpl(firebaseAuth: FirebaseAuth) : AuthRepository {
        return AuthRepositoryImpl(firebaseAuth)
    }
    @Provides
    @Singleton
    fun providesFirestoreRepository(
        tasksDao: TasksDao) : FirestoreRepository {
        return FirestoreRepositoryImpl(Firebase.firestore.apply {
            firestoreSettings = firestoreSettings {
                isPersistenceEnabled = false
            }
        },
            Firebase.analytics,
            Gson(),
            tasksDao
        )
    }
    @Provides
    @Singleton
    fun providesLocationUseCase(): LocationUseCase {
        return LocationUseCase()
    }
}