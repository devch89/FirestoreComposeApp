package com.example.firestorecomposeapp.data.di

import android.content.Context
import android.util.Log
import com.example.firestorecomposeapp.data.local.TasksDB
import com.example.firestorecomposeapp.data.local.TasksDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 *  Hilt Module: informs Hilt how to provide instances of certain types.
 *
 *      Unlike Dagger modules, you must annotate Hilt modules with @InstallIn to tell Hilt which Android class each module will be used or installed in.
 *
 *      https://developer.android.com/training/dependency-injection/hilt-android#hilt-modules
 *
 *  From down to top, it follows the model of an Android app's application graph:
 *
 *      https://developer.android.com/training/dependency-injection/manual
 *
 */


@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun provideSoccerWorldDB(@ApplicationContext context: Context): TasksDB {
        val database: TasksDB by lazy {
            TasksDB.getDatabase(context)
        }
        return database
    }

    @Provides
    fun provideCompetitionDao(database: TasksDB): TasksDao {
        return database.TasksDao()
    }

}