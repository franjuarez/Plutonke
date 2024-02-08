package com.schonke.plutonke.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schonke.plutonke.types.Category
import com.schonke.plutonke.types.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.exp

class SharedDataViewModel() : ViewModel() {
    private val backend = BackendViewModel()
    val isLoading = MutableLiveData<Boolean>()
    private val _sharedExpenses = MutableLiveData<List<Expense>?>(null)
    private val _sharedCategories = MutableLiveData<List<Category>?>(null)

    val sharedExpenses: MutableLiveData<List<Expense>?> get() = _sharedExpenses
    val sharedCategories: MutableLiveData<List<Category>?> get() = _sharedCategories

    private suspend fun loadData() {
        withContext(Dispatchers.IO) {
            try{
                isLoading.postValue(true)
                val expenses = backend.fetchExpenses()
                val categories = backend.fetchCategories()
                _sharedExpenses.postValue(expenses)
                _sharedCategories.postValue(categories)
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    init {
        viewModelScope.launch {
            loadData()
        }
    }

    fun addExpense(expense: Expense){
        val expenses: MutableList<Expense> = (_sharedExpenses.value ?: mutableListOf()).toMutableList()
        expenses.add(expense)
        _sharedExpenses.value = expenses
        //TODO:añadir al back y cambiar la spentAmount de la category
    }

    fun removeExpense(expense: Expense){
        val expenses: MutableList<Expense> = (_sharedExpenses.value ?: mutableListOf()).toMutableList()
        expenses.remove(expense)
        _sharedExpenses.value = expenses
        //TODO: eliminar del back y cambiar la spentAmount de la category
    }
}