package com.example.cashingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.cashingapp.db.CashingDatabase
import com.example.cashingapp.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = CashingDatabase.getInstance(application).transactionDao()

    val todos: LiveData<List<Transaction>> = dao.obtenerTodos()

    fun obtenerPorMes(mes: String): LiveData<List<Transaction>> {
        return dao.obtenerPorMes(mes)
    }

    fun totalIngresosMes(mes: String): LiveData<Double?> {
        return dao.totalIngresosMes(mes)
    }

    fun totalGastosMes(mes: String): LiveData<Double?> {
        return dao.totalGastosMes(mes)
    }

    fun insertar(transaction: Transaction) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertar(transaction)
        }
    }

    fun actualizar(transaction: Transaction) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.actualizar(transaction)
        }
    }

    fun eliminar(transaction: Transaction) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.eliminar(transaction)
        }
    }
}