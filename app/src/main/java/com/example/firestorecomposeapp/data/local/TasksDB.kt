package com.example.firestorecomposeapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Tasks::class], version = 4, exportSchema = false)
abstract class TasksDB: RoomDatabase() {

    abstract fun TasksDao(): TasksDao

    companion object {
        @Volatile
        private var INSTANCE: TasksDB? = null

        fun getDatabase(context: Context): TasksDB {

            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                TasksDB::class.java,
                    "tasks_db"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }

    }

}