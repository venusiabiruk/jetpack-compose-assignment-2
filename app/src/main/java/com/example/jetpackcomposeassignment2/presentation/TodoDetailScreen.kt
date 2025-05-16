package com.example.jetpackcomposeassignment2.presentation

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jetpackcomposeassignment2.data.TodoDatabase
import com.example.jetpackcomposeassignment2.model.Todo
import com.example.jetpackcomposeassignment2.repository.TodoRepository
import com.example.jetpackcomposeassignment2.viewmodel.TodoDetailViewModel
import com.example.jetpackcomposeassignment2.viewmodel.TodoDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    todoId: Int,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current.applicationContext as Application
    val database = TodoDatabase.getDatabase(context)
    val repository = TodoRepository(database.todoDao())
    val viewModel: TodoDetailViewModel = viewModel(
        factory = TodoDetailViewModelFactory(repository)
    )

    LaunchedEffect(todoId) {
        viewModel.loadTodo(todoId)
    }

    val todo by viewModel.todo.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todo Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                error != null -> ErrorState(error = error!!, onRetry = { viewModel.loadTodo(todoId) })
                todo == null -> LoadingState()
                else -> TodoDetails(todo = todo!!)
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Loading todo details...")
        }
    }
}

@Composable
private fun ErrorState(error: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Error loading todo",
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = error)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun TodoDetails(todo: Todo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("ID: ${todo.id}", style = MaterialTheme.typography.titleLarge)
        Text("User ID: ${todo.userId}")
        Text("Title: ${todo.title}")
        Text(
            text = if (todo.completed) "✓ Completed" else "✗ Pending",
            color = if (todo.completed) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }
        )
    }
}