package com.example.firestorecomposeapp.ui.screens.task

import android.app.Activity
import android.app.Notification
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.firestorecomposeapp.data.model.Task
import com.example.firestorecomposeapp.ui.screens.signin.SignInScreenActions
import com.example.firestorecomposeapp.ui.screens.signin.SignInViewModel
import com.example.firestorecomposeapp.util.DataState
import com.example.firestorecomposeapp.viewmodel.FirestoreViewModel
import com.example.location_sdk.LocationApiImpl

@Composable
@RequiresApi(Build.VERSION_CODES.P)
fun TaskScreen(navController: NavController,
               viewModel: FirestoreViewModel = FirestoreViewModel(),
               ){
    val activity = Activity()
    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    //todo Implement Floating Action Button action
                },
            ){
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) {

        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("Task manager","Location SDK")

        Column {
            TabRow(
                selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }

            if ( selectedTabIndex == 0){


                val state = viewModel.viewTasks.value

                when(state){
                    is DataState.LOADING -> {}
                    is DataState.SUCCESS -> {
                        ShowItems(state.response,activity)
                    }
                    is DataState.ERROR -> {}
                }


            } else {
                Column()
                {
                    val context = LocalContext.current
                    val locationApi = LocationApiImpl(context)
                    val notification = Notification()
                    val locationManager = getSystemService(context, LocationManager::class.java)


                    Button(onClick = {
                        locationApi.startTracking(3000L, notification){
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(context, intent, bundleOf(Pair("x","")))
                        }
                    }) {
                        Text(text = "Track Location")
                    }

                    Button(onClick = {
                        locationApi.stopTracking()
                    }) {
                        Text(text = "Stop Tracking")
                    }

                    Button(onClick = {
                        Toast.makeText(context, "${locationApi.isLocationEnabled()}", Toast.LENGTH_SHORT).show()
                    }) {
                        Text(text = "Tracking Status")
                    }

                    Button(onClick = {
                        Toast.makeText(context, "${locationApi.checkPermissions()}", Toast.LENGTH_SHORT).show()
                    }) {
                        Text(text = "Check Permission")
                    }

                    Button(onClick = {
                        locationApi.requestPermissions{
                            if (it){
                                Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
                            }else{
                                ActivityCompat.requestPermissions(
                                    activity,
                                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                                    1
                                )
                            }
                        }
                    }) {
                        Text(text = "Request Permission")
                    }

                    Button(onClick = {
                        locationApi.requestPermissions{
                            if (it){
                                Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
                            }else{
                                ActivityCompat.requestPermissions(
                                    activity,
                                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                                    1
                                )
                            }
                        }
                    }) {
                        Text(text = "Enable Location")
                    }
                }

            }

        }


    }


}

@Composable
fun EntryTaskScreen(viewModel: FirestoreViewModel){
    val task: MutableState<Task> = remember{ mutableStateOf(Task()) }

    Column {
        TextField(
            value = task.value.title ,
            onValueChange = {task.value.copy(title = it) }
        )
        TextField(
            value = task.value.title ,
            onValueChange = {task.value.copy(category = it) }
        )
        TextField(
            value = task.value.title ,
            onValueChange = {task.value.copy(location = it) }
        )
        TextField(
            value = task.value.title ,
            onValueChange = {task.value.copy(time = it) }
        )

        Button(onClick = { viewModel.viewTasks.value }) {
            Text(text = "SAVE")
        }

    }
}

@Composable
fun ShowItems(response: List<Task>, activity: Activity) {
    LazyColumn{
        items(items = response) {
            TaskItem(task = it)
        }
    }
}

@Composable
fun TaskItem(task: Task) {
    Card {
        Column {
            Text(text =task.time)
            Text(text =task.title)
            Text(text =task.category)
            Text(text =task.location)
        }
    }
}

sealed class TaskScreenActions {
    object LoadTask : TaskScreenActions()
}
