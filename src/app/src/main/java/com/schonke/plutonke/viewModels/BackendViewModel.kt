package com.schonke.plutonke.viewModels

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.schonke.plutonke.types.Expense
import com.schonke.plutonke.data.BackendFactory
import com.schonke.plutonke.types.Category
import com.schonke.plutonke.types.ExpenseBackend
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.exp

const val DATE_FORMAT = "dd/MM/yyyy"

@SuppressLint("SimpleDateFormat")
fun convertUnixTimestampToDate(unixTimestamp: Long): String {
    val date = Date(unixTimestamp * 1000) // Convertimos de segundos a milisegundos
    val format = SimpleDateFormat(DATE_FORMAT)
    return format.format(date)
}

@SuppressLint("SimpleDateFormat")
fun convertDateToUnixTimestamp(dateString: String): Long {
    val format = SimpleDateFormat(DATE_FORMAT)
    val date = format.parse(dateString) ?: throw IOException("error converting date")
    return date.time / 1000 // Convertimos de milisegundos a segundos
}

fun changeExpenseToBackendExpense(expense: Expense): ExpenseBackend {
    return ExpenseBackend(
        expense.id,
        expense.name,
        convertDateToUnixTimestamp(expense.date),
        expense.price,
        expense.categoryID
    )
}

private fun changeBackendExpenseToExpense(expenseBackend: ExpenseBackend): Expense {
    return Expense(
        expenseBackend.id,
        expenseBackend.name,
        convertUnixTimestampToDate(expenseBackend.date),
        expenseBackend.price,
        expenseBackend.categoryID
    )
}

class BackendViewModel : ViewModel() {
    private val backend = BackendFactory.getInstance()

    suspend fun fetchExpenses(): List<Expense> {
        val call = backend.getAllExpenses()
        if (call.isSuccessful) {
            val expensesBackend = call.body() ?: emptyList()
            val expenses = mutableListOf<Expense>()
            expensesBackend.forEach { expenseBackend ->
                val expense = changeBackendExpenseToExpense(expenseBackend)
                expenses.add(expense)
            }
            return expenses
        }
        throw IOException("Error fetching Expenses! : ${call.errorBody()?.string()}")
    }



    suspend fun fetchCategories(): List<Category> {
        val call = backend.getAllCategories()
        if (call.isSuccessful) {
            return call.body() ?: emptyList()
        }
        throw IOException("Error fetching Categories! : ${call.errorBody()?.string()}")
    }

    suspend fun addExpense(expense: Expense): Expense? {
        val backendExpense = changeExpenseToBackendExpense(expense)
        val call = backend.addExpense(backendExpense)
        if (call.isSuccessful) {
            if(call.body() == null){
                return null
            }
            return changeBackendExpenseToExpense(call.body()!!)
        }
        return null
    }

    suspend fun editExpense(expense: Expense): Boolean {
        val backendExpense = changeExpenseToBackendExpense(expense)
        val call = backend.updateExpense(expense.id, backendExpense)
        return call.isSuccessful
    }

    suspend fun deleteExpense(id: UInt): Boolean {
        val call = backend.deleteExpense(id)
        return call.isSuccessful
    }
}