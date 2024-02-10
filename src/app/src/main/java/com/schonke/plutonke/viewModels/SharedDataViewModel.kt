package com.schonke.plutonke.viewModels

import androidx.core.text.toSpannable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schonke.plutonke.types.Category
import com.schonke.plutonke.types.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import kotlin.math.exp

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

    private fun changeCategorySpentAmount(categoryName: String, amount: Int){
        val category = _sharedCategories.value?.firstOrNull{ it.name == categoryName }
            ?: throw Exception("Error! Trying to change a Category that doesnt exist.")

        category.spentAmount = category.spentAmount + amount
        dataUpdated.postValue(!dataUpdated.value!!)
    }


    fun addExpense(expense: Expense){
        val expenses: MutableList<Expense> = (_sharedExpenses.value ?: mutableListOf()).toMutableList()
        expenses.add(expense)
        _sharedExpenses.value = expenses
        changeCategorySpentAmount(expense.category, expense.price)
        //TODO:Actualizar el back agregando expense ycambiando category
    }

    fun removeExpense(expense: Expense){
        val expenses: MutableList<Expense> = (_sharedExpenses.value ?: mutableListOf()).toMutableList()
        expenses.remove(expense)
        _sharedExpenses.value = expenses
        //TODO: Actualizar el back borrando el expense y ¿cambiando category? O se puede hacer del back eso...MMM
    }

}