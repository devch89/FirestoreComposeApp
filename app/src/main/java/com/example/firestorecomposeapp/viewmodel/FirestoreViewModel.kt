package com.example.firestorecomposeapp.viewmodel

import android.location.Location
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firestorecomposeapp.firestore.FirestoreRepository
import com.example.firestorecomposeapp.firestore.FirestoreRepositoryImpl
import com.example.firestorecomposeapp.firestore.location.LocationUseCase
import com.example.firestorecomposeapp.data.model.Task
import com.example.firestorecomposeapp.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirestoreViewModel @Inject constructor(
    private val repository: FirestoreRepository,
    private val locationUseCase: LocationUseCase
) :ViewModel() {

    private val _viewTasks: MutableState<DataState> = mutableStateOf(DataState.LOADING)
    val  viewTasks: State<DataState> get() = _viewTasks

    private val _insertState: MutableState<Boolean?> = mutableStateOf(null)
    val  insertState: State<Boolean?> get() = _insertState

    private val _location: MutableState<Location?> = mutableStateOf(null)
    val  location: State<Location?> get() = _location

    init {
        getTasks()
    }

     fun getTasks(){

        viewModelScope.launch {
            repository.tasks.collect{
                _viewTasks.value = it
            }
        }
         repository.getAllTasks()
     }

    fun insertTask(task: Task){
        viewModelScope.launch {
            repository.insertNewTasks(task) {
                _insertState.value = it
            }
        }

    }

    fun retrieveLocation(){
        viewModelScope.launch {
            locationUseCase.locationFlow.collect{
                _location.value = it
            }

        }
    }

    override fun onCleared() {
        repository.cancel()
        super.onCleared()
    }
}