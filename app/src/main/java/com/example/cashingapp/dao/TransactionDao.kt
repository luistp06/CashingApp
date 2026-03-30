package com.example.cashingapp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cashingapp.model.Transaction

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertar(transaction: Transaction)

    @Update
    suspend fun actualizar(transaction: Transaction)

    @Delete
    suspend fun eliminar(transaction: Transaction)

    @Query("SELECT * FROM movimientos WHERE fecha LIKE :mes || '%' ORDER BY fecha DESC")
    fun obtenerPorMes(mes: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM movimientos ORDER BY fecha DESC")
    fun obtenerTodos(): LiveData<List<Transaction>>

    @Query("SELECT SUM(importe) FROM movimientos WHERE tipo = 'INGRESO' AND fecha LIKE :mes || '%'")
    fun totalIngresosMes(mes: String): LiveData<Double?>

    @Query("SELECT SUM(importe) FROM movimientos WHERE tipo = 'GASTO' AND fecha LIKE :mes || '%'")
    fun totalGastosMes(mes: String): LiveData<Double?>
}