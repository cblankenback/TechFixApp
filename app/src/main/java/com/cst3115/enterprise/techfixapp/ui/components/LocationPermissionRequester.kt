package com.cst3115.enterprise.techfixapp.ui.components

import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.*
import androidx.compose.runtime.*
import androidx.compose.material3.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionRequester(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        locationPermissionState.launchMultiplePermissionRequest()
    }

    when {
        locationPermissionState.allPermissionsGranted -> {
            onPermissionGranted()
        }
        locationPermissionState.shouldShowRationale || locationPermissionState.permissions.any { !it.status.isGranted && it.status.shouldShowRationale } -> {
            // Show rationale and request permission again
            PermissionRationaleDialog(
                onRequestPermission = { locationPermissionState.launchMultiplePermissionRequest() },
                onDismiss = { onPermissionDenied() }
            )
        }
        else -> {
            // Permissions denied permanently
            PermissionDeniedDialog(
                onDismiss = { onPermissionDenied() }
            )
        }
    }
}

@Composable
fun PermissionRationaleDialog(
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Location Permission Needed") },
        text = { Text("This app requires location access to show your current location.") },
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
        text = { Text("Without location access, the app cannot display your current location.") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
