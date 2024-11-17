// com.cst3115.enterprise.techfixapp.ui.map.MapScreen.kt

package com.cst3115.enterprise.techfixapp.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cst3115.enterprise.techfixapp.viewmodel.TaskListViewModel
import com.google.accompanist.permissions.*
import com.google.android.gms.location.*
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.cst3115.enterprise.techfixapp.data.model.Task
import com.google.android.gms.maps.CameraUpdateFactory

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    taskListViewModel: TaskListViewModel = viewModel()
) {
    // Collect the list of tasks
    val taskList by taskListViewModel.taskList.collectAsState()

    // Initialize camera position
    val cameraPositionState = rememberCameraPositionState()

    // Initialize location variables
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    LaunchedEffect(taskList) {
        if (taskList.isNotEmpty()) {
            Log.d("MapScreen", "Loaded ${taskList.size} tasks.")
            taskList.forEach { task ->
                Log.d("MapScreen", "Task ID: ${task.id}, Name: ${task.clientName}, Lat: ${task.latitude}, Lon: ${task.longitude}, Completed: ${task.isCompleted}")
            }
        } else {
            Log.d("MapScreen", "No tasks available.")
        }
    }
    // Permission state for location
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // Request permissions on first composition
    LaunchedEffect(Unit) {
        if (!locationPermissionsState.allPermissionsGranted) {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    // Initialize FusedLocationProviderClient
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(
            navController.context as ComponentActivity
        )
    }

    // Location callback for real-time updates
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                if (locationResult.lastLocation != null) {
                    currentLocation = locationResult.lastLocation
                }
            }
        }
    }

    // Start location updates when permissions are granted
    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            val locationRequest = LocationRequest.create().apply {
                interval = 10000 // 10 seconds
                fastestInterval = 5000 // 5 seconds
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        } else {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            currentLocation = null
        }
    }

    // Remember to remove location updates when composable leaves the composition
    DisposableEffect(Unit) {
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // Center the map on the first task's location when taskList changes
    LaunchedEffect(taskList) {
        if (taskList.isNotEmpty()) {
            val firstTask = taskList[0]
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(firstTask.latitude, firstTask.longitude), 12f
            )
        }
    }

// Track if the map has already animated to the current location
    var hasAnimatedToLocation by remember { mutableStateOf(false) }

    LaunchedEffect(currentLocation) {
        currentLocation?.let { location ->
            if (!hasAnimatedToLocation) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(
                        LatLng(location.latitude, location.longitude), 16f
                    ),
                    durationMs = 1000
                )
                hasAnimatedToLocation = true
            }
        }
    }

    // Scaffold with TopAppBar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Client Locations") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = false // We'll handle location display manually
                    ),
                    properties = MapProperties(
                        isMyLocationEnabled = locationPermissionsState.allPermissionsGranted
                    )
                ) {
                    // Add a marker for each task/client
                    taskList.forEach { task ->
                        Marker(

                            state = MarkerState(position = LatLng(task.latitude, task.longitude)),
                            title = task.clientName,
                            snippet = task.jobDescription,
                            icon = BitmapDescriptorFactory.defaultMarker(
                                if (task.isCompleted) BitmapDescriptorFactory.HUE_GREEN
                                else BitmapDescriptorFactory.HUE_RED
                            ),
                            onClick = {
                                // Navigate to TaskDetailScreen when marker is clicked
                                navController.navigate("taskDetail/${task.id}")
                                true // Indicates that the event is consumed
                            }
                        )
                    }

                    // Add a marker for the technician's current location
                    currentLocation?.let { location ->
                        Marker(
                            state = MarkerState(position = LatLng(location.latitude, location.longitude)),
                            title = "You are here",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                        )
                    }
                }

                // Display permission rationale or error
                when {
                    locationPermissionsState.allPermissionsGranted -> {
                        // Permissions granted, do nothing
                    }
                    locationPermissionsState.shouldShowRationale -> {
                        // Show rationale to the user
                        PermissionRationaleDialog(
                            onRequestPermission = { locationPermissionsState.launchMultiplePermissionRequest() },
                            onDismiss = {}
                        )
                    }
                    else -> {
                        // Permissions denied permanently
                        PermissionDeniedDialog(
                            onDismiss = {}
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun PermissionRationaleDialog(
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Location Permission Needed") },
        text = { Text("This app requires location access to show your current location on the map.") },
        confirmButton = {
            TextButton(onClick = onRequestPermission) {
                Text("Grant")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Deny")
            }
        }
    )
}

@Composable
fun PermissionDeniedDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Location Permission Denied") },
        text = { Text("Without location access, the app cannot display your current location on the map.") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}