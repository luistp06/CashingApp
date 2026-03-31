package com.example.cashingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.cashingapp.db.CashingDatabase
import com.example.cashingapp.model.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = CashingDatabase.getInstance(application).categoryDao()

    val categorias: LiveData<List<Category>> = dao.obtenerTodas()

    fun insertar(category: Category) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertar(category)
        }
    }

    fun actualizar(category: Category) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.actualizar(category)
        }
    }

    fun eliminar(category: Category) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.eliminar(category)
        }
    }
}