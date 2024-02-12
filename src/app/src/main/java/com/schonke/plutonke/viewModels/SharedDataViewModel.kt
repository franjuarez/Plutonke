package com.schonke.plutonke.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schonke.plutonke.states.LoadDataState
import com.schonke.plutonke.types.Category
import com.schonke.plutonke.types.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import kotlin.math.exp

class SharedDataViewModel: ViewModel() {
    private val backend = BackendViewModel()

    val dataUpdated = MutableLiveData(false)
    private val _loadingState = MutableStateFlow<LoadDataState>(LoadDataState.Loading)
    val loadingState: StateFlow<LoadDataState> = _loadingState

    private val _sharedExpenses = MutableLiveData<List<Expense>>(emptyList())
    private val _sharedCategories = MutableLiveData<List<Category>>(emptyList())
    val sharedExpenses: MutableLiveData<List<Expense>> get() = _sharedExpenses
    val sharedCategories: MutableLiveData<List<Category>> get() = _sharedCategories

    private suspend fun loadData() {
        try {
            _loadingState.value = LoadDataState.Loading
            val expenses = backend.fetchExpenses()
            val categories = backend.fetchCategories()
            _sharedExpenses.postValue(expenses)
            _sharedCategories.postValue(categories)
            _loadingState.value = LoadDataState.Success
        } catch (error: IOException){
            _loadingState.value = LoadDataState.Error(msg = "error fetching data: ${error.message}")
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

    fun addExpense(expense: Expense, isExpenseValid: MutableStateFlow<LoadDataState>) {
        viewModelScope.launch(Dispatchers.IO) {
            val newExpense = backend.addExpense(expense)
            if (newExpense == null){
                isExpenseValid.value = LoadDataState.Error("could not add new expense") //TODO: errores descriptivos
            } else{
                val expenses: MutableList<Expense> =
                    (_sharedExpenses.value ?: mutableListOf()).toMutableList()
                expenses.add(newExpense)
                _sharedExpenses.postValue(expenses)
                changeCategorySpentAmount(newExpense.category, newExpense.price)
                isExpenseValid.value = LoadDataState.Success
            }
        }
    }
//ver de meter early returns
//    fun removeExpense(id: String){
//        viewModelScope.launch(Dispatchers.IO) {
//            val confirm = backend.deleteExpense(id)
//            if (!confirm){
//                _expenseValidState.value = LoadDataState.Error(
//                    "Could not delete expense") //TODO: errores descriptivos
//            } else{
//                val expenses: MutableList<Expense> =
//                    (_sharedExpenses.value ?: mutableListOf()).toMutableList()
//                val expense = expenses.find { it.id == id } //search for  same id
//                if(expense == null){
//                    _expenseValidState.value = LoadDataState.Error(
//                        "Could not delete expense; it doesnt exist") //TODO: errores descriptivos
//                } else {
//                    val category = expense.category
//                    val price = expense.price * -1
//                    expenses.remove(expense)
//                    _sharedExpenses.postValue(expenses)
//                    changeCategorySpentAmount(category, price)
//                    _expenseValidState.value = LoadDataState.Success
//                }
//            }
//        }
//    }

}