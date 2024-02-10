package com.schonke.plutonke.viewModels

import androidx.lifecycle.ViewModel
import com.schonke.plutonke.types.Expense
import com.schonke.plutonke.data.BackendFactory
import com.schonke.plutonke.types.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class BackendViewModel : ViewModel() {
    private val backend = BackendFactory.getInstance()

    suspend fun fetchExpenses(): List<Expense>? {
        return withContext(Dispatchers.IO){
            val call = backend.getAllExpenses()
            if(call.isSuccessful){
                return@withContext call.body()
            }
            throw IOException("Error fetching Expenses! : ${call.errorBody()?.string()}")
        }
    }

    suspend fun fetchCategories(): List<Category>?{
        return withContext(Dispatchers.IO){
            val call = backend.getAllCategories()
            if(call.isSuccessful){
                return@withContext call.body()
            }
            throw IOException("Error fetching Categories! : ${call.errorBody()?.string()}")
        }
    }
}