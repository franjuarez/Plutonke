package com.schonke.plutonke.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    private fun changeCategorySpentAmount(categoryID: UInt, amount: Float) {
        val category = _sharedCategories.value?.find { it.id == categoryID}
            ?: throw Exception("Error! Trying to change a Category that doesn't exist.")

        category.spentAmount = category.spentAmount + amount
        dataUpdated.postValue(!dataUpdated.value!!)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addExpense(expense: Expense, isExpenseValid: MutableStateFlow<LoadDataState>) {
        viewModelScope.launch(Dispatchers.IO) {
            val newExpense = backend.addExpense(expense)
            if (newExpense == null) {
                println("error del server")
                isExpenseValid.value =
                    LoadDataState.Error("Could not add new expense") //TODO: errores descriptivos
                return@launch
            }

            val expenses: MutableList<Expense> =
                (_sharedExpenses.value ?: mutableListOf()).toMutableList()
            addExpenseAtOrderedPosition(expenses, newExpense)
            _sharedExpenses.postValue(expenses)
            changeCategorySpentAmount(newExpense.categoryID, newExpense.price)
            isExpenseValid.value = LoadDataState.Success("Expense added")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isAfter(date1: String, date2: String): Boolean {
        val format = DateTimeFormatter.ofPattern(DATE_FORMAT)
        val newDate1 = LocalDate.parse(date1, format)
        val newDate2 = LocalDate.parse(date2, format)

        return newDate1.isAfter(newDate2)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addExpenseAtOrderedPosition(
        expenses: MutableList<Expense>,
        newExpense: Expense
    ) {
        val expenseDate = newExpense.date
        var left =   0
        var right = expenses.size -   1
        var position = expenses.size

        while (left <= right) {
            val mid = (left + right) /   2
            if (isAfter(expenses[mid].date, expenseDate)) {
                left = mid +   1
            } else {
                right = mid -   1
                position = mid
            }
        }
        expenses.add(position, newExpense)
    }


    fun removeExpense(id: UInt, isExpenseValid: MutableStateFlow<LoadDataState>) {
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

            val categoryID = expense.categoryID
            val price = expense.price * -1
            expenses.remove(expense)
            _sharedExpenses.postValue(expenses)
            changeCategorySpentAmount(categoryID, price)
            isExpenseValid.value = LoadDataState.Success("Expense deleted")
        }
    }


    fun modifyExpense(
        id: UInt,
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
        if (oldExpense.categoryID == modifiedExpense.categoryID) {
            val difference = modifiedExpense.price - oldExpense.price
            changeCategorySpentAmount(modifiedExpense.categoryID, difference)
        } else {
            changeCategorySpentAmount(oldExpense.categoryID, oldExpense.price * -1)
            changeCategorySpentAmount(modifiedExpense.categoryID, modifiedExpense.price)
        }
    }
}