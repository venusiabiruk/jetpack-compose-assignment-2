package com.example.jetpackcomposeassignment2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeassignment2.model.Todo
import com.example.jetpackcomposeassignment2.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TodoDetailViewModel(
    private val repository: TodoRepository
) : ViewModel() {

    private val _todo = MutableStateFlow<Todo?>(null)
    val todo: StateFlow<Todo?> = _todo

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadTodo(id: Int) {
        viewModelScope.launch {
            try {
                _todo.value = repository.getTodoById(id).first()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load todo: ${e.message}"
            }
        }
    }
}