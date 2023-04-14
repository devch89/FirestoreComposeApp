package com.example.firestorecomposeapp.ui.screens.task

import android.app.Activity
import android.app.Notification
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.example.firestorecomposeapp.data.model.Task
import com.example.firestorecomposeapp.ui.theme.ltgray_dot
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
fun TaskScreen(viewModel: FirestoreViewModel, activity: Activity) {
    val context = LocalContext.current
    val locationApi = LocationApiImpl(context)
    val notification = Notification()

    locationApi.requestPermissions {
        if (it) {
            Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT)
                .show()
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
            Text(text = "Add tas", color = Color.White, modifier = Modifier.padding(7.dp))
        }

    }
}