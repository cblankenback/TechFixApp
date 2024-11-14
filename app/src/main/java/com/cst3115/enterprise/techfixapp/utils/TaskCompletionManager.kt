package com.cst3115.enterprise.techfixapp.utils


import android.content.Context
import android.content.SharedPreferences

class TaskCompletionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "task_completion_prefs"
        private const val KEY_PREFIX = "task_completed_"
    }

    /**
     * Saves the completion status of a task.
     *
     * @param taskId The unique identifier of the task.
     * @param isCompleted The completion status to save.
     */
    fun setTaskCompleted(taskId: Int, isCompleted: Boolean) {
        prefs.edit().putBoolean(KEY_PREFIX + taskId, isCompleted).apply()
    }

    /**
     * Retrieves the completion status of a task.
     *
     * @param taskId The unique identifier of the task.
     * @return True if the task is completed, False otherwise.
     */
    fun isTaskCompleted(taskId: Int): Boolean {
        return prefs.getBoolean(KEY_PREFIX + taskId, false)
    }
}