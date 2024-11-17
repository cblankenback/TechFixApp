// com.cst3115.enterprise.techfixapp.data.repository.TaskRepository.kt

package com.cst3115.enterprise.techfixapp.data.repository

import android.content.Context
import com.cst3115.enterprise.techfixapp.data.model.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.cst3115.enterprise.techfixapp.utils.TaskCompletionManager
import java.io.File

class TaskRepository(private val context: Context) {

    private val gson = Gson()
    private val fileName = "tasks.json"
    private val file: File = File(context.filesDir, fileName)
    private val taskCompletionManager = TaskCompletionManager(context)

    /**
     * Retrieves the list of tasks from the local JSON file.
     * Updates each task's completion status based on SharedPreferences.
     *
     * @return A list of tasks with updated completion statuses.
     */
    suspend fun getTasks(): List<Task> {
        return withContext(Dispatchers.IO) {
            if (!file.exists()) {
                // Copy tasks.json from assets to internal storage
                context.assets.open(fileName).use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            val jsonString = file.bufferedReader().use { it.readText() }
            val taskListType = object : TypeToken<List<Task>>() {}.type
            val tasks: List<Task> = gson.fromJson(jsonString, taskListType)

            // Update each task's isCompleted status based on SharedPreferences
            val updatedTasks = tasks.map { task ->
                task.copy(isCompleted = taskCompletionManager.isTaskCompleted(task.id))
            }

            updatedTasks // Return the updated task list
        }
    }


    /**
     * Updates the completion status of a task both in SharedPreferences and the JSON file.
     *
     * @param taskId The unique identifier of the task.
     * @param isCompleted The new completion status.
     */
    suspend fun updateTaskStatus(taskId: Int, isCompleted: Boolean) {
        withContext(Dispatchers.IO) {
            // Update SharedPreferences
            taskCompletionManager.setTaskCompleted(taskId, isCompleted)

            // Optionally, update the JSON file if you want to persist the status there as well
            // For this implementation, we're handling status via SharedPreferences only
            // If you prefer to update the JSON file as well, uncomment the following lines:

//            val tasks = getTasks().toMutableList()
//            val index = tasks.indexOfFirst { it.id == taskId }
//            if (index != -1) {
//                tasks[index] = tasks[index].copy(isCompleted = isCompleted)
//                saveTasks(tasks)
//            }

        }
    }

    /**
     * Saves the list of tasks back to the JSON file.
     *
     * @param tasks The list of tasks to save.
     */
    private suspend fun saveTasks(tasks: List<Task>) {
        withContext(Dispatchers.IO) {
            val jsonString = gson.toJson(tasks)
            file.writeText(jsonString)
        }
    }
}
