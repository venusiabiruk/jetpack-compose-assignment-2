package com.example.jetpackcomposeassignment2.presentation

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jetpackcomposeassignment2.data.TodoDatabase
import com.example.jetpackcomposeassignment2.model.Todo
import com.example.jetpackcomposeassignment2.repository.TodoRepository
import com.example.jetpackcomposeassignment2.viewmodel.TodoListViewModel
import com.example.jetpackcomposeassignment2.viewmodel.TodoListViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(navController: NavController) {
    val context = LocalContext.current.applicationContext as Application
    val database = remember { TodoDatabase.getDatabase(context) }
    val repository = remember { TodoRepository(database.todoDao()) }
    val viewModel: TodoListViewModel = viewModel(
        factory = TodoListViewModelFactory(repository)
    )

    val todos by viewModel.todos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Todo List",
                        color = Color.White // Title text color
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.loadTodos() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary // Top bar background color
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                isLoading -> LoadingView()
                error != null -> ErrorView(error!!) { viewModel.loadTodos() }
                todos.isEmpty() -> EmptyView()
                else -> TodoListView(todos, navController)
            }
        }
    }
}

@Composable
private fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorView(error: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun EmptyView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No todos found")
    }
}

@Composable
private fun TodoListView(todos: List<Todo>, navController: NavController) {
    LazyColumn {
        items(todos) { todo ->
            TodoItem(todo = todo) {
                navController.navigate("detail/${todo.id}")
            }
        }
    }
}

@Composable
private fun TodoItem(todo: Todo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (todo.completed) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(todo.title)
            Text(
                if (todo.completed) "Completed" else "Pending",
                color = if (todo.completed) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}
