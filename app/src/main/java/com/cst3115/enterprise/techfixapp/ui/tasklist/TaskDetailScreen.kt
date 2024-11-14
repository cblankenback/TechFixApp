// ui/taskdetail/TaskDetailScreen.kt

package com.cst3115.enterprise.techfixapp.ui.tasklist

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cst3115.enterprise.techfixapp.data.model.Task
import com.cst3115.enterprise.techfixapp.viewmodel.TaskDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Int,
    navController: NavController,
    taskDetailViewModel: TaskDetailViewModel = viewModel()
) {
    val task by taskDetailViewModel.getTask(taskId).collectAsState(initial = null)
    val isUpdating by taskDetailViewModel.isUpdating.collectAsState()
    LaunchedEffect(Unit) {
        taskDetailViewModel.getTask(taskId)
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply paddingValues to avoid overlap with the top bar
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
            } ?: run {
                // Show a loading or error state if task is null
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
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
            .fillMaxSize()
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
