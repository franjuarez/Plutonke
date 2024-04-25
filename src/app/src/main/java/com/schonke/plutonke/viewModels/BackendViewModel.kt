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

const val UNEXPECTED_SERVER_ERROR = "Unexpected server error"
private const val EMPTY_CATEGORY_SERVER_RESPONSE = "Unexpected server response. Did not receive category response"

class BackendViewModel : ViewModel() {
    private val backend = BackendFactory.getInstance()
    suspend fun fetchExpenses(): Resource<List<Expense>> {
        val call = backend.getAllExpenses()
        val result = call.body()
        return try {
            if (call.isSuccessful) {
                val expenses = result?.map { changeBackendExpenseToExpense(it) } ?: emptyList()
                Resource.Success(expenses)
            } else {
                val errBody = call.errorBody()?.string() ?: ""
                val errorResponse = fromJsonGeneric<List<ValidationError>>(errBody)
                Resource.ErrorValidation(errorResponse)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: UNEXPECTED_SERVER_ERROR)
        }
    }

    suspend fun fetchCategories(): Resource<List<Category>> {
        val call = backend.getAllCategories()
        val result = call.body()
        return try {
            if (call.isSuccessful) {
                Resource.Success(result ?: emptyList())
            } else {
                val errBody = call.errorBody()?.string() ?: ""
                val errorResponse = fromJsonGeneric<List<ValidationError>>(errBody)
                Resource.ErrorValidation(errorResponse)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: UNEXPECTED_SERVER_ERROR)
        }
    }

    suspend fun addExpense(expense: Expense): Resource<Expense> {
        val backendExpense = changeExpenseToBackendExpense(expense)
        val call = backend.addExpense(backendExpense)
        val result = call.body()
        return try {
            if (call.isSuccessful) {
                if (result != null) {
                    Resource.Success(changeBackendExpenseToExpense(result))
                } else {
                    println("[#Debug]: Server Error: addExpense result is NULL")
                    Resource.Error(message = "Unexpected server response. Did not receive expense response")
                }
            } else {
                val errBody = call.errorBody()?.string() ?: ""
                val errorResponse = fromJsonGeneric<List<ValidationError>>(errBody)
                Resource.ErrorValidation(errorResponse)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: UNEXPECTED_SERVER_ERROR)
        }
    }

    suspend fun editExpense(expense: Expense): Resource<Expense> {
        val backendExpense = changeExpenseToBackendExpense(expense)
        println("Gasto con el q llamo a updateExpense: $backendExpense")
        val call = backend.updateExpense(expense.id, backendExpense)
        val result = call.body()
        return try {
            if (call.isSuccessful) {
                if (result != null) {
                    Resource.Success(changeBackendExpenseToExpense(result))
                } else {
                    println("[#Debug]: Server Error: editExpense result is NULL")
                    Resource.Error("Unexpected server response")
                }
            } else {
                val errBody = call.errorBody()?.string() ?: ""
                val errorResponse = fromJsonGeneric<List<ValidationError>>(errBody)
                Resource.ErrorValidation(errorResponse)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: UNEXPECTED_SERVER_ERROR)
        }
    }

    suspend fun deleteExpense(id: UInt): Resource<Unit> {
        val call = backend.deleteExpense(id)
        return try {
            if (call.isSuccessful) {
                Resource.Success(Unit)
            } else {
                val errBody = call.errorBody()?.string() ?: ""
                val errorResponse = fromJsonGeneric<List<ValidationError>>(errBody)
                Resource.ErrorValidation(errorResponse)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: UNEXPECTED_SERVER_ERROR)
        }
    }

    suspend fun addCategory(category: Category): Resource<Category> {
        val call = backend.addCategory(category)
        val result = call.body()
        return try {
            if (call.isSuccessful) {
                if (result != null) {
                    Resource.Success(result)
                } else {
//                    println("[#Debug]: Server Error: addCategory result is NULL")
                    Resource.Error(message = EMPTY_CATEGORY_SERVER_RESPONSE)
                }
            } else {
                val errBody = call.errorBody()?.string() ?: ""
                val errorResponse = fromJsonGeneric<List<ValidationError>>(errBody)
                Resource.ErrorValidation(errorResponse)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: UNEXPECTED_SERVER_ERROR)
        }
    }

    suspend fun editCategory(category: Category): Resource<Category> {
        val call = backend.updateCategory(category.id, category)
        val result = call.body()
        return try {
            if (call.isSuccessful) {
                if (result != null) {
                    Resource.Success(result)
                } else {
//                    println("[#Debug]: Server Error: editExpense result is NULL")
                    Resource.Error(EMPTY_CATEGORY_SERVER_RESPONSE)
                }
            } else {
                val errBody = call.errorBody()?.string() ?: ""
                val errorResponse = fromJsonGeneric<List<ValidationError>>(errBody)
                Resource.ErrorValidation(errorResponse)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: UNEXPECTED_SERVER_ERROR)
        }
    }

    suspend fun deleteCategory(id: UInt): Resource<Unit> {
        val call = backend.deleteCategory(id)
        return try {
            if (call.isSuccessful) {
                Resource.Success(Unit)
            } else {
                val errBody = call.errorBody()?.string() ?: ""
                val errorResponse = fromJsonGeneric<List<ValidationError>>(errBody)
                Resource.ErrorValidation(errorResponse)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: UNEXPECTED_SERVER_ERROR)
        }
    }
}