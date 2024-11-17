package com.cst3115.enterprise.techfixapp.ui.tasklist
// ui/tasklist/TaskListScreen.kt

import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cst3115.enterprise.techfixapp.viewmodel.LoginViewModel
import com.cst3115.enterprise.techfixapp.data.model.Task
import com.cst3115.enterprise.techfixapp.viewmodel.TaskListViewModel

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun TaskListScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel(),
    taskListViewModel: TaskListViewModel = viewModel()
) {
    val taskList by taskListViewModel.taskList.collectAsState()
    val isLoading by taskListViewModel.isLoading.collectAsState()
    LaunchedEffect(Unit) {
        taskListViewModel.fetchTasks()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Today's Tasks") },
                actions = {
                    IconButton(onClick = {
                        // Handle logout
                        loginViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("taskList") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                    IconButton(onClick = {
                        // Navigate to the Map Screen
                        navController.navigate("map")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Map"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            // Show a loading indicator
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(taskList.size) { index ->
                    val task = taskList[index]
                    TaskItem(
                        task = task,
                        onTaskClick = {
                            // Navigate to TaskDetailScreen with the taskId
                            navController.navigate("taskDetail/${task.id}")
                        },
                        onMarkAsComplete = {
                            taskListViewModel.markTaskAsCompleted(task.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onMarkAsComplete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onTaskClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (task.isCompleted) Icons.Default.TaskAlt else Icons.Default.Task,
                contentDescription = null,
                tint = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.clientName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = task.address,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = task.jobDescription,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (!task.isCompleted) {
                Button(
                    onClick = onMarkAsComplete
                ) {
                    Text("Complete")
                }
            } else {
                Text(
                    text = "Completed",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}