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

class SharedDataViewModel : ViewModel() {
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
            _loadingState.value = LoadDataState.Success("Data loaded")
        } catch (error: IOException) {
            _loadingState.value = LoadDataState.Error(msg = "error fetching data: ${error.message}")
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadData()
        }
    }

    private fun changeCategorySpentAmount(categoryName: String, amount: Float) {
        val category = _sharedCategories.value?.firstOrNull { it.name == categoryName }
            ?: throw Exception("Error! Trying to change a Category that doesn't exist.")

        category.spentAmount = category.spentAmount + amount
        dataUpdated.postValue(!dataUpdated.value!!)
    }

    fun addExpense(expense: Expense, isExpenseValid: MutableStateFlow<LoadDataState>) {
        viewModelScope.launch(Dispatchers.IO) {
            val newExpense = backend.addExpense(expense)
            if (newExpense == null) {
                isExpenseValid.value =
                    LoadDataState.Error("could not add new expense") //TODO: errores descriptivos
                return@launch
            }

            val expenses: MutableList<Expense> =
                (_sharedExpenses.value ?: mutableListOf()).toMutableList()
            expenses.add(newExpense)
            _sharedExpenses.postValue(expenses)
            changeCategorySpentAmount(newExpense.category, newExpense.price)
            isExpenseValid.value = LoadDataState.Success("Expense added")
        }
    }

    //ver de meter early returns
    fun removeExpense(id: String, isExpenseValid: MutableStateFlow<LoadDataState>) {
        viewModelScope.launch(Dispatchers.IO) {
            val confirm = backend.deleteExpense(id)
            if (!confirm) {
                isExpenseValid.value = LoadDataState.Error("Could not delete expense")
                return@launch
            }

            val expenses: MutableList<Expense> =
                (_sharedExpenses.value ?: mutableListOf()).toMutableList()
            val expense = expenses.find { it.id == id } // Busca el gasto por su id

            if (expense == null) {
                isExpenseValid.value =
                    LoadDataState.Error("Could not delete expense, it does not exist")
                return@launch
            }

            val category = expense.category
            val price = expense.price * -1
            expenses.remove(expense)
            _sharedExpenses.postValue(expenses)
            changeCategorySpentAmount(category, price)
            isExpenseValid.value = LoadDataState.Success("Expense deleted")
        }
    }


    fun modifyExpense(
        id: String,
        modifiedExpense: Expense,
        isExpenseValid: MutableStateFlow<LoadDataState>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val expenses: MutableList<Expense> =
                (_sharedExpenses.value ?: mutableListOf()).toMutableList()
            val expenseIndex = expenses.indexOfFirst { it.id == id }
            if (expenseIndex == -1) {
                isExpenseValid.value = LoadDataState.Error(
                    "Expense doess not exist!"
                )
                return@launch
            }
            val oldExpense = expenses[expenseIndex]
            val confirm = backend.editExpense(modifiedExpense)
            if (!confirm) {
                isExpenseValid.value = LoadDataState.Error(
                    "Could not edit expense"
                )
                return@launch
            }
            modifyLocalExpense(oldExpense, modifiedExpense)
            expenses[expenseIndex] = modifiedExpense
            _sharedExpenses.postValue(expenses)
            isExpenseValid.value = LoadDataState.Success("Expense modified")
        }
    }

    private fun modifyLocalExpense(
        oldExpense: Expense,
        modifiedExpense: Expense
    ) {
        if (oldExpense.category == modifiedExpense.category) {
            val difference = modifiedExpense.price - oldExpense.price
            changeCategorySpentAmount(modifiedExpense.category, difference)
        } else {
            changeCategorySpentAmount(oldExpense.category, oldExpense.price * -1)
            changeCategorySpentAmount(modifiedExpense.category, modifiedExpense.price)
        }
    }
}