package com.schonke.plutonke.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schonke.plutonke.states.LoadMainDataState
import com.schonke.plutonke.types.Category
import com.schonke.plutonke.types.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception
import kotlin.math.exp

class SharedDataViewModel() : ViewModel() {
    private val backend = BackendViewModel()

    val dataUpdated = MutableLiveData(false)
    private val _loadingState = MutableStateFlow<LoadMainDataState>(LoadMainDataState.Loading)
    val loadingState: StateFlow<LoadMainDataState> = _loadingState

    private val _expenseValidState = MutableStateFlow<LoadMainDataState>(LoadMainDataState.Loading)
    val expenseValidState: StateFlow<LoadMainDataState> = _expenseValidState

    private val _sharedExpenses = MutableLiveData<List<Expense>>(emptyList())
    private val _sharedCategories = MutableLiveData<List<Category>>(emptyList())
    val sharedExpenses: MutableLiveData<List<Expense>> get() = _sharedExpenses
    val sharedCategories: MutableLiveData<List<Category>> get() = _sharedCategories

    private suspend fun loadData() {
        try {
            _loadingState.value = LoadMainDataState.Loading
            val expenses = backend.fetchExpenses()
            val categories = backend.fetchCategories()
            _sharedExpenses.postValue(expenses)
            _sharedCategories.postValue(categories)
            _loadingState.value = LoadMainDataState.Success
        } catch (error: IOException){
            _loadingState.value = LoadMainDataState.Error(msg = "error fetching data: ${error.message}")
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadData()
        }
    }

    private fun changeCategorySpentAmount(categoryName: String, amount: Float){
        val category = _sharedCategories.value?.firstOrNull{ it.name == categoryName }
            ?: throw Exception("Error! Trying to change a Category that doesn't exist.")

        category.spentAmount = category.spentAmount + amount
        dataUpdated.postValue(!dataUpdated.value!!)
    }

    fun addExpense(expenseName: String, expenseDate: String, expensePrice: String, expenseCategory: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val price = expensePrice.replace(",", ".")
                .toFloatOrNull()

            if(price == null){
                _expenseValidState.value = LoadMainDataState.Error("error") //TODO: errores descriptivos
                return@launch
            }

            val expense =
                backend.addExpense(
                    Expense(
                        "0",
                        expenseName,
                        expenseDate,
                        price,
                        expenseCategory
                    )
                )
            if (expense == null){
                _expenseValidState.value = LoadMainDataState.Error("error") //TODO: errores descriptivos
            } else{
                val expenses: MutableList<Expense> =
                    (_sharedExpenses.value ?: mutableListOf()).toMutableList()
                expenses.add(expense)
                _sharedExpenses.postValue(expenses)
                changeCategorySpentAmount(expense.category, expense.price)
                _expenseValidState.value = LoadMainDataState.Success
            }
        }
    }

    fun resetExpenseValidState(){
        _expenseValidState.value = LoadMainDataState.Loading
    }
    fun removeExpense(expense: Expense){
        val expenses: MutableList<Expense> = (_sharedExpenses.value ?: mutableListOf()).toMutableList()
        expenses.remove(expense)
        _sharedExpenses.value = expenses
        //TODO: Actualizar el back borrando el expense y ¿cambiando category?
    }

}