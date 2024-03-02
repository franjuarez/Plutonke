package com.schonke.plutonke.viewModels

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.schonke.plutonke.data.Resource
import com.schonke.plutonke.types.Expense
import com.schonke.plutonke.data.BackendFactory
import com.schonke.plutonke.errors.ValidationError
import com.schonke.plutonke.types.Category
import com.schonke.plutonke.types.ExpenseBackend
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date

const val DATE_FORMAT = "dd/MM/yyyy"

@SuppressLint("SimpleDateFormat")
fun convertUnixTimestampToDate(unixTimestamp: Long): String {
    val date = Date(unixTimestamp * 1000)
    val format = SimpleDateFormat(DATE_FORMAT)
    return format.format(date)
}

@SuppressLint("SimpleDateFormat")
fun convertDateToUnixTimestamp(dateString: String): Long {
    val format = SimpleDateFormat(DATE_FORMAT)
    val date = format.parse(dateString) ?: throw IOException("error converting date")
    return date.time / 1000
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

inline fun <reified T> fromJsonGeneric(json: String): T {
    val type = object : TypeToken<T>() {}.type
    return Gson().fromJson(json, type)
}

class BackendViewModel : ViewModel() {
    private val backend = BackendFactory.getInstance()
    suspend fun fetchExpenses(): Resource<List<Expense>> {
        val call = backend.getAllExpenses()
        val result = call.body()
        return try {
            if (call.isSuccessful) {
                val expensesBackend = fromJsonGeneric<List<ExpenseBackend>>(result.toString())
                val expenses = expensesBackend.map { changeBackendExpenseToExpense(it) }
                Resource.Success(expenses)
            } else {
                val errorResponse = fromJsonGeneric<List<ValidationError>>(result.toString())
                Resource.ErrorValidation(errorResponse)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unexpected server error")
        }
    }

    suspend fun fetchCategories(): Resource<List<Category>> {
        val call = backend.getAllCategories()
        val result = call.body()
        return try {
            if (call.isSuccessful) {
                val categories = fromJsonGeneric<List<Category>>(result.toString())
                Resource.Success(categories)
            } else {
                val errorResponse = fromJsonGeneric<List<ValidationError>>(result.toString())
                Resource.ErrorValidation(errorResponse)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unexpected server error")
        }
    }

    suspend fun addExpense(expense: Expense): Resource<Expense> {
        val backendExpense = changeExpenseToBackendExpense(expense)
        val call = backend.addExpense(backendExpense)
        val result = call.body()
        return try {
            if (call.isSuccessful) {
                val backendExpenseResponse = fromJsonGeneric<ExpenseBackend>(result.toString())
                Resource.Success(changeBackendExpenseToExpense(backendExpenseResponse))
//                val backendExpenseResponse = call.body()
//                if (backendExpenseResponse?.data == null) {
//                    Resource.Error("Unexpected server response")
//                } else {
//                    Resource.Success(changeBackendExpenseToExpense(backendExpenseResponse.data))
//                }
            } else {
                val errBody = call.errorBody()?.string() ?: ""
                println(errBody)
                val errorResponse = fromJsonGeneric<List<ValidationError>>(errBody!!)
                println(errorResponse) //NULl!!!!!
                Resource.ErrorValidation(errorResponse)
//                val errorResponse = call.body()?.errorResponse ?: emptyList()
//                Resource.ErrorValidation(errorResponse)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unexpected server error")
        }
    }


    suspend fun editExpense(expense: Expense): Resource<Expense> {
        val backendExpense = changeExpenseToBackendExpense(expense)
        val call = backend.updateExpense(expense.id, backendExpense)
        return try {
            if (call.isSuccessful) {
                val backendExpenseResponse = call.body()
                if (backendExpenseResponse?.data == null) {
                    Resource.Error("Unexpected server response")
                } else {
                    Resource.Success(changeBackendExpenseToExpense(backendExpenseResponse.data))
                }
            } else {
                val errorResponse = call.body()?.errorResponse ?: emptyList()
                Resource.ErrorValidation(errorResponse)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unexpected server error")
        }
    }

    suspend fun deleteExpense(id: UInt): Resource<Unit> {
        val call = backend.deleteExpense(id)
        return try {
            if (call.isSuccessful) {
                Resource.Success(Unit)
            } else {
                val errorResponse = call.body()?.errorResponse ?: emptyList()
                Resource.ErrorValidation(errorResponse)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unexpected server error")
        }
    }
}