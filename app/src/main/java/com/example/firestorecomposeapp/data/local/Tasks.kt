package com.example.firestorecomposeapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tasks")
data class Tasks(@PrimaryKey @ColumnInfo(name = "taskCollection") val taskCollection:  String,
                 @ColumnInfo(name = "title") val title: String = "",
                 @ColumnInfo(name = "time") val time: String = "",
                 @ColumnInfo(name = "category") val category: String = "",
                 @ColumnInfo(name = "location") val location: String = ""
                       ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tasks

        if (taskCollection != other.taskCollection) return false

        return true
    }

    override fun hashCode(): Int {
        return taskCollection.hashCode()
    }


}