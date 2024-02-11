package com.schonke.plutonke.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schonke.plutonke.types.Category
import com.schonke.plutonke.types.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class SharedDataViewModel() : ViewModel() {
    private val backend = BackendViewModel()
    val isLoading = MutableLiveData<Boolean>()
    private val _sharedExpenses = MutableLiveData<List<Expense>?>(null)
    private val _sharedCategories = MutableLiveData<List<Category>?>(null)
    val sharedExpenses: MutableLiveData<List<Expense>?> get() = _sharedExpenses
    val sharedCategories: MutableLiveData<List<Category>?> get() = _sharedCategories

    val dataUpdated = MutableLiveData(false)

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

    private fun changeCategorySpentAmount(categoryName: String, amount: Float){
        val category = _sharedCategories.value?.firstOrNull{ it.name == categoryName }
            ?: throw Exception("Error! Trying to change a Category that doesnt exist.")

        category.spentAmount = category.spentAmount + amount
        dataUpdated.postValue(!dataUpdated.value!!)
    }


    fun addExpense(expenseData: Expense){
        //TODO:Actualizar el back agregando expense ycambiando category
        //Aca me devuelve el id y cambio el de mi Expense
        val id = "53"
        val expense = Expense(id, expenseData.name, expenseData.date, expenseData.price, expenseData.category)
        val expenses: MutableList<Expense> = (_sharedExpenses.value ?: mutableListOf()).toMutableList()
        expenses.add(expense)
        _sharedExpenses.value = expenses
        changeCategorySpentAmount(expense.category, expense.price)
    }

    fun removeExpense(expense: Expense){
        val expenses: MutableList<Expense> = (_sharedExpenses.value ?: mutableListOf()).toMutableList()
        expenses.remove(expense)
        _sharedExpenses.value = expenses
        //TODO: Actualizar el back borrando el expense y ¿cambiando category?
    }

}