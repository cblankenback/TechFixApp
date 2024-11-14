package com.cst3115.enterprise.techfixapp.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cst3115.enterprise.techfixapp.data.model.Task
import com.cst3115.enterprise.techfixapp.data.repository.TaskRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskListViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository = TaskRepository(application.applicationContext)

    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    val taskList: StateFlow<List<Task>> = _taskList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchTasks()
    }

    /**
     * Fetches tasks from the repository and updates the task list.
     */
    fun fetchTasks() {
        _isLoading.value = true
        viewModelScope.launch {
            val tasks = taskRepository.getTasks()
            _taskList.value = tasks
            _isLoading.value = false
        }
    }

    /**
     * Marks a task as completed and refreshes the task list.
     *
     * @param taskId The unique identifier of the task.
     */
    fun markTaskAsCompleted(taskId: Int) {
        viewModelScope.launch {
            taskRepository.updateTaskStatus(taskId, true)
            // Refresh the task list after updating
            fetchTasks()
        }
    }
}