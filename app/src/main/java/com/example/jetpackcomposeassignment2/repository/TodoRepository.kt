package com.example.jetpackcomposeassignment2.repository

import com.example.jetpackcomposeassignment2.data.TodoDao
import com.example.jetpackcomposeassignment2.model.Todo
import com.example.jetpackcomposeassignment2.network.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import java.io.IOException

class TodoRepository(private val dao: TodoDao) {
    val todos: Flow<List<Todo>> = dao.getAll()
        .catch { e ->
            throw IOException("Failed to load todos from database", e)
        }

    suspend fun refreshTodos() {
        try {
            val remoteTodos = RetrofitInstance.api.getTodos()
            dao.deleteAll()
            dao.insertAll(remoteTodos)
        } catch (e: Exception) {
            throw IOException("Failed to refresh todos from network", e)
        }
    }

    fun getTodoById(id: Int): Flow<Todo> = flow {
        try {
            // First try to get from database
            val localTodo = try {
                dao.getById(id).first()
            } catch (e: Exception) {
                null
            }

            if (localTodo != null) {
                emit(localTodo)

                // Check for updates from network
                try {
                    val remoteTodo = RetrofitInstance.api.getTodoById(id)
                    if (remoteTodo != localTodo) {
                        dao.insert(remoteTodo)
                        emit(remoteTodo)
                    }
                } catch (e: Exception) {
                    // Continue with local data if network fails
                }
            } else {
                // No local data, fetch from network
                try {
                    val remoteTodo = RetrofitInstance.api.getTodoById(id)
                    dao.insert(remoteTodo)
                    emit(remoteTodo)
                } catch (e: Exception) {
                    throw IOException("No local or remote data available for todo $id", e)
                }
            }
        } catch (e: Exception) {
            throw IOException("Failed to load todo with ID $id", e)
        }
    }.catch { e ->
        throw IOException("Error in todo flow for ID $id", e)
    }
}