package com.schonke.plutonke.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schonke.plutonke.data.Resource
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

const val UNABLE_TO_FETCH_DATA = "error fetching data"
const val SERVER_ERROR = "Server error"
const val UNEXISTING_EXPENSE_ERROR = "expense does not exist"
const val UNEXISTING_CATEGORY_ERROR = "category does not exist"

private const val CATEGORY_SUCCESFULLY_ADDED = "Category added"
private const val EXPENSE_SUCCESFULLY_ADDED = "Expense added"
private const val EXPENSE_SUCCESFULLY_DELETED = "Expense deleted"
private const val EXPENSE_SUCCESFULLY_MODIFIED = "Expense modified"
private const val CATEGORY_SUCCESFULLY_DELETED = "Category deleted"
private const val CATEGORY_SUCCESFULLY_MODIFIED = "Category modified"

class SharedDataViewModel : ViewModel() {
    private val backend = BackendViewModel()

    val dataUpdated = MutableLiveData(false)
    private val _loadingState = MutableStateFlow<LoadDataState>(LoadDataState.Loading)
    val loadingState: StateFlow<LoadDataState> = _loadingState

    private val _sharedExpenses = MutableLiveData<List<Expense>>(emptyList())
    private val _sharedCategories = MutableLiveData<List<Category>>(emptyList())
    val sharedExpenses: MutableLiveData<List<Expense>> get() = _sharedExpenses
    val sharedCategories: MutableLiveData<List<Category>> get() = _sharedCategories

    private fun changeLoadingStateToError(message: String?) {
        _loadingState.value = LoadDataState.Error(msg = "$UNABLE_TO_FETCH_DATA: $message")
    }

    private suspend fun loadData() {
        try {
            _loadingState.value = LoadDataState.Loading
            val expenses = backend.fetchExpenses()
            val categories = backend.fetchCategories()

            if (expenses is Resource.Success && categories is Resource.Success) {
                _sharedExpenses.postValue(expenses.data!!)
                _sharedCategories.postValue(categories.data!!)
                _loadingState.value = LoadDataState.Success("Data loaded")
            } else {
                val errorMessage =
                    if (expenses is Resource.Error || expenses is Resource.ErrorValidation) {
                        expenses.message
                    } else {
                        categories.message
                    }
                changeLoadingStateToError(errorMessage)
            }
        } catch (error: IOException) {
            changeLoadingStateToError(error.message)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadData()
        }
    }

    private fun notifyDataUpdated() {
        dataUpdated.postValue(!dataUpdated.value!!)
    }

    private fun changeCategorySpentAmount(categoryID: UInt, amount: Float) {
        val category = _sharedCategories.value?.find { it.id == categoryID }
            ?: throw Exception("Error! Trying to change a Category that doesn't exist.")
        category.spentAmount = category.spentAmount + amount
        notifyDataUpdated()
    }

    private fun modifyLocalCategorySpentAmount(
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
        var left = 0
        var right = expenses.size - 1
        var position = expenses.size

        while (left <= right) {
            val mid = (left + right) / 2
            if (isAfter(expenses[mid].date, expenseDate)) {
                left = mid + 1
            } else {
                right = mid - 1
                position = mid
            }
        }
        expenses.add(position, newExpense)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addExpense(expense: Expense, isExpenseValid: MutableStateFlow<LoadDataState>) {
        viewModelScope.launch(Dispatchers.IO) {
            println(expense)
            when (val response = backend.addExpense(expense)) {
                is Resource.Error -> {
                    isExpenseValid.value = LoadDataState.Error(SERVER_ERROR)
                }
                is Resource.ErrorValidation -> isExpenseValid.value =
                    LoadDataState.ErrorValidating(response.errorResponse!!)
                is Resource.Success -> {
                    val newExpense = response.data!!
                    val expenses: MutableList<Expense> =
                        (_sharedExpenses.value ?: mutableListOf()).toMutableList()
                    addExpenseAtOrderedPosition(expenses, newExpense)
                    _sharedExpenses.postValue(expenses)
                    changeCategorySpentAmount(newExpense.categoryID, newExpense.price)
                    isExpenseValid.value = LoadDataState.Success(EXPENSE_SUCCESFULLY_ADDED)
                }
            }
        }
    }

    fun removeExpense(id: UInt, isExpenseValid: MutableStateFlow<LoadDataState>) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = backend.deleteExpense(id)) {
                is Resource.Error -> {
                    isExpenseValid.value = LoadDataState.Error(SERVER_ERROR)
                }
                is Resource.ErrorValidation -> isExpenseValid.value =
                    LoadDataState.ErrorValidating(response.errorResponse!!)
                is Resource.Success -> {
                    val expenses: MutableList<Expense> =
                        (_sharedExpenses.value ?: mutableListOf()).toMutableList()
                    val expense = expenses.find { it.id == id }
                    if (expense == null) {
                        isExpenseValid.value = LoadDataState.Error(UNEXISTING_EXPENSE_ERROR)
                        return@launch
                    }
                    val categoryID = expense.categoryID
                    val price = expense.price * -1
                    expenses.remove(expense)
                    _sharedExpenses.postValue(expenses)
                    changeCategorySpentAmount(categoryID, price)
                    isExpenseValid.value = LoadDataState.Success(EXPENSE_SUCCESFULLY_DELETED)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                isExpenseValid.value = LoadDataState.Error(UNEXISTING_EXPENSE_ERROR)
                return@launch
            }
            println("Gasto en modifyExpense: $modifiedExpense")
            when (val response = backend.editExpense(modifiedExpense)) {
                is Resource.Error -> {
                    isExpenseValid.value = LoadDataState.Error(SERVER_ERROR)
                }
                is Resource.ErrorValidation -> isExpenseValid.value =
                    LoadDataState.ErrorValidating(response.errorResponse!!)
                is Resource.Success -> {
                    val oldExpense = expenses[expenseIndex]
                    modifyLocalCategorySpentAmount(oldExpense, modifiedExpense)
                    expenses[expenseIndex] = modifiedExpense
                    _sharedExpenses.postValue(expenses)
                    isExpenseValid.value = LoadDataState.Success(EXPENSE_SUCCESFULLY_MODIFIED)
                }
            }
        }
    }

    fun addCategory(category: Category, isCategoryValid: MutableStateFlow<LoadDataState>) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = backend.addCategory(category)) {
                is Resource.Error -> {
                    isCategoryValid.value = LoadDataState.Error(SERVER_ERROR)
                }
                is Resource.ErrorValidation -> isCategoryValid.value =
                    LoadDataState.ErrorValidating(response.errorResponse!!)
                is Resource.Success -> {
                    val newCategory = response.data!!
                    val categories: MutableList<Category> =
                        (_sharedCategories.value ?: mutableListOf()).toMutableList()
//                    addExpenseAtOrderedPosition(expenses, newExpense)
                    categories.add(newCategory)
                    _sharedCategories.postValue(categories)
                    notifyDataUpdated()
                    isCategoryValid.value = LoadDataState.Success(CATEGORY_SUCCESFULLY_ADDED)
                }
            }
        }
    }

    fun removeCategory(id: UInt, isCategoryValid: MutableStateFlow<LoadDataState>) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = backend.deleteCategory(id)) {
                is Resource.Error -> {
                    isCategoryValid.value = LoadDataState.Error(SERVER_ERROR)
                }
                is Resource.ErrorValidation -> isCategoryValid.value =
                    LoadDataState.ErrorValidating(response.errorResponse!!)
                is Resource.Success -> {
                    val categories: MutableList<Category> =
                        (_sharedCategories.value ?: mutableListOf()).toMutableList()
                    val category = categories.find { it.id == id }
                    if (category == null) {
                        isCategoryValid.value = LoadDataState.Error(UNEXISTING_CATEGORY_ERROR)
                        return@launch
                    }
                    categories.remove(category)
                    _sharedCategories.postValue(categories)
                    notifyDataUpdated()
                    isCategoryValid.value = LoadDataState.Success(CATEGORY_SUCCESFULLY_DELETED)
                }
            }
        }
    }

    fun modifyCategory(
        id: UInt,
        modifiedCategory: Category,
        isCategoryValid: MutableStateFlow<LoadDataState>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val categories: MutableList<Category> =
                (_sharedCategories.value ?: mutableListOf()).toMutableList()
            val categoryIndex = categories.indexOfFirst { it.id == id }
            if (categoryIndex == -1) {
                isCategoryValid.value = LoadDataState.Error(UNEXISTING_EXPENSE_ERROR)
                return@launch
            }
            when (val response = backend.editCategory(modifiedCategory)) {
                is Resource.Error -> {
                    isCategoryValid.value = LoadDataState.Error(SERVER_ERROR)
                }
                is Resource.ErrorValidation -> isCategoryValid.value =
                    LoadDataState.ErrorValidating(response.errorResponse!!)
                is Resource.Success -> {
                    categories[categoryIndex] = modifiedCategory
                    _sharedCategories.postValue(categories)
                    notifyDataUpdated()
                    isCategoryValid.value = LoadDataState.Success(CATEGORY_SUCCESFULLY_MODIFIED)
                }
            }
        }
    }

}