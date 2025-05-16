package com.example.jetpackcomposeassignment2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeassignment2.model.Todo
import com.example.jetpackcomposeassignment2.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

class TodoListViewModel(
    private val repository: TodoRepository
) : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadTodos()
    }

    fun loadTodos() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.refreshTodos()
                _error.value = null

                // Collect in a separate coroutine so it doesn't block finally
                launch {
                    repository.todos.collectLatest { todoList ->
                        _todos.value = todoList
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to load todos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
            fun loadTodos() {
                _isLoading.value = true
                viewModelScope.launch {
                    try {
                        repository.refreshTodos()
                        _error.value = null

                        // Collect in a separate coroutine so it doesn't block finally
                        launch {
                            repository.todos.collectLatest { todoList ->
                                _todos.value = todoList
                            }
                        }
                    } catch (e: Exception) {
                        _error.value = "Failed to load todos: ${e.message}"
                    } finally {
                        _isLoading.value = false
                    }
                }
            }
        }
    }
}