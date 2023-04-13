package com.example.firestorecomposeapp

import android.app.Activity
import android.app.Notification
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.example.firestorecomposeapp.firestore.location.LocationReceiver
import com.example.firestorecomposeapp.firestore.location.LocationReceiver.Companion.intentFilter
import com.example.firestorecomposeapp.data.model.Task
import com.example.firestorecomposeapp.ui.theme.FirestoreComposeAppTheme
import com.example.firestorecomposeapp.util.DataState
import com.example.firestorecomposeapp.viewmodel.FirestoreViewModel
import com.example.location_sdk.LocationApiImpl

class MainActivity : ComponentActivity() {

    private val firestoreViewModel by lazy{
        ViewModelProvider(this)[FirestoreViewModel::class.java]
    }

    private val locationReceiver by lazy {
        LocationReceiver()
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirestoreComposeAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(firestoreViewModel, this)


                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(locationReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(
            locationReceiver
        )
    }
}



@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MainScreen(viewModel: FirestoreViewModel, activity: Activity){
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

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FirestoreComposeAppTheme {
        Greeting("Android")
    }
}