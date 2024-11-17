// ui/taskdetail/TaskDetailScreen.kt

package com.cst3115.enterprise.techfixapp.ui.tasklist

import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cst3115.enterprise.techfixapp.BuildConfig
import com.cst3115.enterprise.techfixapp.data.model.Task
import com.cst3115.enterprise.techfixapp.ui.components.LocationPermissionRequester
import com.cst3115.enterprise.techfixapp.viewmodel.TaskDetailViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TaskDetailScreen(
    taskId: Int,
    navController: NavController,
    taskDetailViewModel: TaskDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val task by taskDetailViewModel.getTask(taskId).collectAsState(initial = null)
    val isUpdating by taskDetailViewModel.isUpdating.collectAsState()
    var currentLocation by remember { mutableStateOf<Location?>(null) }

    // Permission handling
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // Request permissions if not granted
    LaunchedEffect(Unit) {
        if (!locationPermissionsState.allPermissionsGranted) {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    // Fetch location if permissions are granted
    if (locationPermissionsState.allPermissionsGranted) {
        val fusedLocationClient = remember {
            LocationServices.getFusedLocationProviderClient(context)
        }

        LaunchedEffect(fusedLocationClient) {
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        currentLocation = location
                    }
                    .addOnFailureListener {
                        currentLocation = null
                    }
            } catch (e: SecurityException) {
                currentLocation = null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp).verticalScroll(rememberScrollState()), // Additional padding to ensure spacing from the edges
            verticalArrangement = Arrangement.spacedBy(16.dp) // Space between elements
        ) {
            task?.let { task ->
                TaskDetailContent(
                    task = task,
                    isUpdating = isUpdating,
                    onMarkAsComplete = {
                        taskDetailViewModel.markTaskAsCompleted(task.id)
                    },
                    onMarkAsNotComplete = {
                        taskDetailViewModel.markTaskAsNotCompleted(task.id)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
                Log.d("TaskDetailScreen", "Current Location: $currentLocation")
                // Display current location
                if (currentLocation != null) {
                    Text(
                        text = "Current Location:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location Icon",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lat: ${currentLocation!!.latitude}, Lon: ${currentLocation!!.longitude}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    Text(
                        text = "Current location not available.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Button to re-request permissions if denied
                if (!locationPermissionsState.allPermissionsGranted) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = {
                        locationPermissionsState.launchMultiplePermissionRequest()
                    }) {
                        Text("Grant Location Permissions")
                    }
                }
            } ?: run {
                // Show a loading or error state if task is null
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}


@Composable
fun TaskDetailContent(
    task: Task,
    isUpdating: Boolean,
    onMarkAsComplete: () -> Unit,
    onMarkAsNotComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Client Name: ${task.clientName}",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Address: ${task.address}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Job Description: ${task.jobDescription}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Status: ${if (task.isCompleted) "Completed" else "Pending"}",
            style = MaterialTheme.typography.bodyLarge,
            color = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(24.dp))
        if (!task.isCompleted) {
            Button(
                onClick = onMarkAsComplete,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUpdating
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Completing...")
                } else {
                    Text("Mark as Complete")
                }
            }
        } else {
            Button(
                onClick = onMarkAsNotComplete,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUpdating,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reverting...")
                } else {
                    Text("Mark as Not Complete")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This task has been completed.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}