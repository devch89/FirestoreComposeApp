package com.example.firestorecomposeapp.ui.screens.task

import android.app.Activity
import android.app.Notification
import android.content.Intent
import android.location.LocationManager
import android.os.Build

import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.firestorecomposeapp.MainActivity
import com.example.firestorecomposeapp.data.model.Task
import com.example.firestorecomposeapp.ui.theme.ltgray_dot
import com.example.firestorecomposeapp.util.DataState
import com.example.firestorecomposeapp.viewmodel.FirestoreViewModel
import com.example.location_sdk.LocationApiImpl
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalDateTime

private const val TAG = "TaskScreen"

@Composable
fun AddTaskToTaskScreen(viewModel: FirestoreViewModel, activity: Activity) {
    val context = LocalContext.current
    val locationApi = LocationApiImpl(context)
    val notification = Notification()

    locationApi.requestPermissions {
        if (it) {
            Log.d(TAG, "AddTaskToTaskScreen: Permission Granted")
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }
    locationApi.startTracking(3000L, notification) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        ContextCompat.startActivity(context, intent, bundleOf(Pair("x", "")))
    }
    val location = locationApi.getLastKnownLocation()

    var title by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 30.dp, end = 30.dp, top = 30.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        TextField(
            value = title,
            onValueChange = {
                title = it
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                cursorColor = Color.Black,
                disabledLabelColor = ltgray_dot, unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ), shape = RoundedCornerShape(8.dp), singleLine = true, placeholder = {
                Text(text = "Title")
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = category,
            onValueChange = {
                category = it
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                cursorColor = Color.Black,
                disabledLabelColor = ltgray_dot, unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ), shape = RoundedCornerShape(8.dp), singleLine = true, placeholder = {
                Text(text = "Category")
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {

                val date = LocalDateTime.now().toString()

                runBlocking {
                    val loc = location.first()
                    Log.d(TAG, "TaskScreen: location${location.first()}")
                    scope.launch {
                        viewModel.insertTask(
                            Task(
                                title = title,
                                category = category,
                                time = date,
                                location = "${loc.latitude},${loc.longitude}"
                            )
                        )
                        title = ""
                        category = ""
                    }

                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 30.dp, end = 30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black, contentColor = Color.White
            ),
            shape = RoundedCornerShape(15.dp)
        ) {
            Text(text = "Add task", color = Color.White, modifier = Modifier.padding(7.dp))
        }

    }
}

@Composable
@RequiresApi(Build.VERSION_CODES.P)
fun TaskScreen(
    navController: NavController,
    viewModel: FirestoreViewModel = hiltViewModel(),
){
    val activity = Activity()
    Scaffold() {

        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("Task manager","Adding/Tracking")

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
                    val activityTask = context as MainActivity


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
                    AddTaskToTaskScreen(viewModel = viewModel, activity = activityTask )
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
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Text(
                text = task.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.Gray)
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            Text(text = task.category, fontSize = 20.sp)
            Text(text = task.time, fontStyle = FontStyle.Italic)
            Text(text = task.location, fontStyle = FontStyle.Italic)
        }
    }
}

sealed class TaskScreenActions {
    object LoadTask : TaskScreenActions()
}
