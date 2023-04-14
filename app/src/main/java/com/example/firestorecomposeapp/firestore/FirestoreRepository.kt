package com.example.firestorecomposeapp.firestore

import android.util.Log
import androidx.core.os.bundleOf
import com.example.firestorecomposeapp.data.local.Tasks
import com.example.firestorecomposeapp.data.local.TasksDao
import com.example.firestorecomposeapp.data.model.Task
import com.example.firestorecomposeapp.util.DataState
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

private const val TAG = "FirestoreRepository"

interface FirestoreRepository {
    val tasks:  StateFlow<DataState>
    fun getAllTasks()
    fun insertNewTasks(task: Task, result: (Boolean) -> Unit)
    fun cancel()
}

class FirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val analytics: FirebaseAnalytics,
    private val gson: Gson,
    private val tasksDao: TasksDao
) : FirestoreRepository{

    private val _tasks: MutableStateFlow<DataState> = MutableStateFlow(DataState.LOADING)
    override val tasks: StateFlow<DataState>
        get()= _tasks

    private val coroutineScope = CoroutineScope(Dispatchers.IO)


    override  fun getAllTasks() {

        saveToLocalFromRemote()
        retrieveDataFromLocalStorage()

    }

    private fun saveToLocalFromRemote() {
        analytics.logEvent(FirebaseAnalytics.Param.ITEM_ID, bundleOf(
            Pair("EVENT", "LOADING"),
        ))

        firestore.collection("taskCollection")
            .get()
            .addOnSuccessListener {
                val data = gson.toJson(
                    it.toObjects(Task::class.java),
                    object: TypeToken<List<Task>>(){}.type)

                Log.d(TAG, "getAllTasks: $data")

                coroutineScope.launch {
                    for (document in it.documents){
                        tasksDao.insert(
                            Tasks(document.id,
                                document.data?.get("title").toString(),
                                document.data?.get("time").toString(),
                                document.data?.get("category").toString(),
                                document.data?.get("location").toString())
                        )
                    }

                }
                analytics.logEvent(FirebaseAnalytics.Param.ITEM_ID, bundleOf(
                    Pair("EVENT", "SUCCESS"),
                    Pair("DATA",
                        gson.toJson(
                            it.toObjects(Task::class.java),
                            object: TypeToken<List<Task>>(){}.type)
                    )
                ))
            }

            .addOnFailureListener {
                Log.d(TAG, "getAllTasks: fail ${it.message}")
                _tasks.value = DataState.ERROR(it)
                analytics.logEvent(FirebaseAnalytics.Param.ITEM_ID, bundleOf(
                    Pair("EVENT", "ERROR"),
                    Pair("EVENT", it.localizedMessage)
                ))
            }
    }
    private fun retrieveDataFromLocalStorage() {
        coroutineScope.launch {
            Log.d(TAG, "getAllTasks: retrieveDataFromLocalStorage")
            tasksDao.getAllTasks().collect{ tasks ->
                _tasks.value = DataState.SUCCESS(tasks.map {
                    Task(it.title,it.time,it.category,it.location)
                })

            }

        }

    }
    override fun cancel(){
        coroutineScope.cancel()
    }

    override  fun insertNewTasks(task: Task, result: (Boolean) -> Unit) {
        firestore.collection("tasksCollection")
            .document(task.title)
            .set(task)
            .addOnSuccessListener {
                result(true)
                Log.d(TAG, "insertNewTasks: Item inserted correctly!")
            }
            .addOnFailureListener {
                result(false)
                Log.d(TAG, "insertNewTasks: ${it.localizedMessage}", it)
            }

    }

}