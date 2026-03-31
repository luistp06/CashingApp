package com.example.cashingapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cashingapp.dao.CategoryDao
import com.example.cashingapp.dao.TransactionDao
import com.example.cashingapp.model.Category
import com.example.cashingapp.model.Transaction

@Database(entities = [Category::class, Transaction::class], version = 1)
abstract class CashingDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: CashingDatabase? = null

        fun getInstance(context: Context): CashingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CashingDatabase::class.java,
                    "cashing_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}