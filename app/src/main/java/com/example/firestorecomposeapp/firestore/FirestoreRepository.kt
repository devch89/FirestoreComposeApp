package com.example.firestorecomposeapp.firestore

import android.util.Log
import androidx.core.os.bundleOf
import com.example.firestorecomposeapp.data.model.Task
import com.example.firestorecomposeapp.util.DataState
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "FirestoreRepository"

interface FirestoreRepository {
    val tasks:  StateFlow<DataState>
     fun getAllTasks()
     fun insertNewTasks(task: Task, result: (Boolean) -> Unit)
}

class FirestoreRepositoryImpl(
    private val firestore: FirebaseFirestore = Firebase.firestore,
    private val analytics: FirebaseAnalytics = Firebase.analytics,
    private val gson: Gson = Gson()
) : FirestoreRepository{

    private val _tasks: MutableStateFlow<DataState> = MutableStateFlow(DataState.LOADING)
    override val tasks: StateFlow<DataState>
    get()= _tasks

    override  fun getAllTasks() {

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

                _tasks.value = DataState.SUCCESS(it.toObjects(Task::class.java))

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