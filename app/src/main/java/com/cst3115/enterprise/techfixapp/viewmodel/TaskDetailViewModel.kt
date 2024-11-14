package com.cst3115.enterprise.techfixapp.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cst3115.enterprise.techfixapp.data.model.Task
import com.cst3115.enterprise.techfixapp.data.repository.TaskRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
class TaskDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository = TaskRepository(application.applicationContext)

    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating

    /**
     * Fetches a specific task by its ID.
     *
     * @param taskId The unique identifier of the task.
     * @return A StateFlow containing the task.
     */
    fun getTask(taskId: Int): StateFlow<Task?> {
        viewModelScope.launch {
            val tasks = taskRepository.getTasks()
            _task.value = tasks.find { it.id == taskId }
        }
        return _task
    }

    /**
     * Marks a task as completed and updates the task flow.
     *
     * @param taskId The unique identifier of the task.
     */
    fun markTaskAsCompleted(taskId: Int) {
        viewModelScope.launch {
            _isUpdating.value = true
            taskRepository.updateTaskStatus(taskId, true)
            // Fetch the updated task
            val tasks = taskRepository.getTasks()
            _task.value = tasks.find { it.id == taskId }
            _isUpdating.value = false
        }
    }

    /**
     * Marks a task as not completed and updates the task flow.
     *
     * @param taskId The unique identifier of the task.
     */
    fun markTaskAsNotCompleted(taskId: Int) {
        viewModelScope.launch {
            _isUpdating.value = true
            taskRepository.updateTaskStatus(taskId, false)
            // Fetch the updated task
            val tasks = taskRepository.getTasks()
            _task.value = tasks.find { it.id == taskId }
            _isUpdating.value = false
        }
    }
}
