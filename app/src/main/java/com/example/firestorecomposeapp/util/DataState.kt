package com.example.firestorecomposeapp.util

import com.example.firestorecomposeapp.data.model.Task

sealed class DataState{
    object LOADING: DataState()
    data class SUCCESS(val response: List<Task>): DataState()
    data class ERROR(val error: Exception) : DataState()
}
