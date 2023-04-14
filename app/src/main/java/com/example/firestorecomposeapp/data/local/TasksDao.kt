package com.example.firestorecomposeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.firestorecomposeapp.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tasks: Tasks)

    @Query("Select * from tasks")
    fun getAllTasks(): Flow<List<Tasks>>

}