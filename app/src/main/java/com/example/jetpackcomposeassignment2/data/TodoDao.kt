package com.example.jetpackcomposeassignment2.data

import androidx.room.*
import com.example.jetpackcomposeassignment2.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM Todo")
    fun getAll(): Flow<List<Todo>>

    @Query("SELECT * FROM Todo WHERE id = :id")
    fun getById(id: Int): Flow<Todo?>  // ✅ updated to return nullable Todo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(todos: List<Todo>)

    @Query("DELETE FROM Todo")
    suspend fun deleteAll()
}