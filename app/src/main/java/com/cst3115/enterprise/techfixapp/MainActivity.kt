package com.cst3115.enterprise.techfixapp
// MainActivity.kt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.NavType

import androidx.navigation.compose.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.cst3115.enterprise.techfixapp.ui.login.LoginScreen
import com.cst3115.enterprise.techfixapp.ui.tasklist.TaskListScreen
import com.cst3115.enterprise.techfixapp.ui.theme.TechFixAppTheme
import com.cst3115.enterprise.techfixapp.viewmodel.LoginViewModel
import com.cst3115.enterprise.techfixapp.ui.tasklist.TaskDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TechFixAppTheme {
                val navController = rememberNavController()
                val loginViewModel: LoginViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = if (loginViewModel.checkLoginState()) {
                        "taskList"
                    } else {
                        "login"
                    }
                ) {
                    composable("login") {
                        LoginScreen(onLoginSuccess = {
                            navController.navigate("taskList") {
                                // Clear the back stack to prevent returning to the login screen
                                popUpTo("login") { inclusive = true }
                            }
                        }, loginViewModel = loginViewModel)
                    }
                    composable("taskList") {
                        TaskListScreen(navController = navController, loginViewModel = loginViewModel)
                    }
                    composable(
                        route = "taskDetail/{taskId}",
                        arguments = listOf(navArgument("taskId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
                        TaskDetailScreen(taskId = taskId, navController = navController)
                    }
                }
            }
        }
    }
}