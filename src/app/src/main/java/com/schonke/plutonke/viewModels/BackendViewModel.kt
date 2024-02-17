package com.schonke.plutonke.viewModels

import androidx.lifecycle.ViewModel
import com.schonke.plutonke.types.Expense
import com.schonke.plutonke.data.BackendFactory
import com.schonke.plutonke.types.Category
import java.io.IOException

class BackendViewModel : ViewModel() {
    private val backend = BackendFactory.getInstance()

    suspend fun fetchExpenses(): List<Expense> {
        val call = backend.getAllExpenses()
        if(call.isSuccessful){
            return call.body() ?: emptyList()
        }
        throw IOException("Error fetching Expenses! : ${call.errorBody()?.string()}")
    }

    suspend fun fetchCategories(): List<Category>{
        val call = backend.getAllCategories()
        if(call.isSuccessful){
            return call.body() ?: emptyList()
        }
        throw IOException("Error fetching Categories! : ${call.errorBody()?.string()}")
    }

    suspend fun addExpense(expense: Expense): Expense?{
        val call = backend.addExpense(expense)
        if(call.isSuccessful){
            return call.body()
        }
        return null
    }

    suspend fun editExpense(expense: Expense): Boolean{
        val call = backend.updateExpense(expense.id, expense)
        return call.isSuccessful
    }

    suspend fun deleteExpense(id: UInt): Boolean{
        val call = backend.deleteExpense(id)
        return call.isSuccessful
    }
}