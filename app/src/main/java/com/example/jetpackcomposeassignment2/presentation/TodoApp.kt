package com.example.jetpackcomposeassignment2.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcomposeassignment2.presentation.TodoDetailScreen
import com.example.jetpackcomposeassignment2.presentation.TodoListScreen

@Composable
fun TodoApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            TodoListScreen(navController)
        }
        composable("detail/{todoId}") { backStackEntry ->
            val todoId = backStackEntry.arguments?.getString("todoId")?.toIntOrNull() ?: return@composable
            TodoDetailScreen(todoId = todoId, navController = navController)
        }
    }
}
