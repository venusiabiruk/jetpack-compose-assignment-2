package com.example.jetpackcomposeassignment2.network

import com.example.jetpackcomposeassignment2.model.Todo
import retrofit2.http.GET
import retrofit2.http.Path

interface TodoApi {
    @GET("todos")
    suspend fun getTodos(): List<Todo>

    @GET("todos/{id}")
    suspend fun getTodoById(@Path("id") id: Int): Todo
}