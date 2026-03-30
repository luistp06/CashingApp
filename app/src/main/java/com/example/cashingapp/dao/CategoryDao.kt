package com.example.cashingapp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cashingapp.model.Category

@Dao
interface CategoryDao {

    @Insert
    suspend fun insertar(category: Category)

    @Update
    suspend fun actualizar(category: Category)

    @Delete
    suspend fun eliminar(category: Category)

    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    fun obtenerTodas(): LiveData<List<Category>>
}