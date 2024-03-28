package com.schonke.plutonke.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.schonke.plutonke.errors.ValidationError
import com.schonke.plutonke.screens.EXPENSE_CATEGORY_FIELD_ERR
import com.schonke.plutonke.screens.EXPENSE_DATE_FIELD_ERR
import com.schonke.plutonke.screens.EXPENSE_NAME_FIELD_ERR
import com.schonke.plutonke.screens.EXPENSE_PRICE_FIELD_ERR
import com.schonke.plutonke.states.LoadDataState
import com.schonke.plutonke.types.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val VALID_DATE_LENGTH = 10
private const val DATE_TOKEN = '/'
private const val REQUIERED_FIELD = "This field can't be empty!"
private const val INVALID_DATE = "Invalid date! Remember the format is dd/mm/YYYY"


class ExpensesViewModel(
    private val dataViewModel: SharedDataViewModel,
) : ViewModel() {

    private val _expenseName = MutableLiveData<String>()
    val expenseName: LiveData<String> = _expenseName

    private val _expenseDate = MutableLiveData<String>()
    val expenseDate: LiveData<String> = _expenseDate

    private val _expensePrice = MutableLiveData<String>()
    val expensePrice: LiveData<String> = _expensePrice

    private val _expenseCategoryID = MutableLiveData<UInt>()
    val expenseCategoryID: LiveData<UInt> = _expenseCategoryID

    private val _expenseValidState = MutableStateFlow<LoadDataState>(LoadDataState.Loading)
    val expenseValidState: StateFlow<LoadDataState> = _expenseValidState

    fun resetExpense() {
        _expenseName.value = ""
        _expenseDate.value = ""
        _expensePrice.value = ""
        _expenseCategoryID.value = 0U
        resetExpenseValidState()
    }

    fun resetExpenseValidState() {
        _expenseValidState.value = LoadDataState.Loading
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun expenseHasInvalidFields(): Boolean {
        return (_expenseName.value == null || _expenseName.value == "" ||
                _expensePrice.value == null || _expensePrice.value == "" ||
                _expenseDate.value == null || _expenseDate.value == "" ||
                !isValidDate(_expenseDate.value ?: "") ||
                _expenseCategoryID.value == null || _expenseCategoryID.value == 0U
                )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showInvalidFieldError() {
        val validationErrors = mutableListOf<ValidationError>()
        if (_expenseName.value == null || _expenseName.value == "") {
            val validationError = ValidationError(EXPENSE_NAME_FIELD_ERR, REQUIERED_FIELD)
            validationErrors.add(validationError)
        }
        if (_expensePrice.value == null || _expensePrice.value == "") {
            val validationError = ValidationError(EXPENSE_PRICE_FIELD_ERR, REQUIERED_FIELD)
            validationErrors.add(validationError)
        }
        if (_expenseDate.value == null || _expenseDate.value == "") {
            val validationError = ValidationError(EXPENSE_DATE_FIELD_ERR, REQUIERED_FIELD)
            validationErrors.add(validationError)
        } else if (!isValidDate(_expenseDate.value ?: "")) {
            val validationError = ValidationError(EXPENSE_DATE_FIELD_ERR, INVALID_DATE)
            validationErrors.add(validationError)
        }
        if (_expenseCategoryID.value == null || _expenseCategoryID.value == 0U) {
            val validationError = ValidationError(EXPENSE_CATEGORY_FIELD_ERR, REQUIERED_FIELD)
            validationErrors.add(validationError)
        }
        _expenseValidState.value = LoadDataState.ErrorValidating(validationErrors)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onConfirmPressed() {
        if (!expenseHasInvalidFields()) {
            val price = expensePrice.value?.replace(",", ".")
                ?.toFloatOrNull() ?: 0.0f
            dataViewModel.addExpense(
                Expense(
                    0U,
                    expenseName.value!!,
                    expenseDate.value!!,
                    price,
                    expenseCategoryID.value!!
                ), _expenseValidState
            )
        } else {
            showInvalidFieldError()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onModifiedPressed(id: UInt) {
        if (expenseHasInvalidFields()) {
            val price = expensePrice.value?.replace(",", ".")
                ?.toFloatOrNull() ?: 0.0f
            dataViewModel.modifyExpense(
                id,
                Expense(
                    id, expenseName.value!!,
                    expenseDate.value!!,
                    price,
                    expenseCategoryID.value!!
                ),
                _expenseValidState
            )
        }
    }

    fun onDeletePressed(id: UInt) {
        dataViewModel.removeExpense(id, _expenseValidState)
    }

    fun onNameChanged(name: String) {
        _expenseName.value = name
    }

    fun onDateChanged(date: String) {
        if (isFormingValidDate(date)) {
            _expenseDate.value = date
        }
    }

    fun onPriceChanged(price: String) {
        if (isFormingValidPrice(price)) {
            _expensePrice.value = price
        }
    }

    fun onCategoryChanged(categoryID: UInt) {
        _expenseCategoryID.value = categoryID
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isValidDate(date: String): Boolean {
        if (date == "") {
            return false
        }
        return try {
            val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
            val localDate = LocalDate.parse(date, formatter)
            !(!formatter.format(localDate).equals(date) || localDate > LocalDate.now())
        } catch (e: Exception) {
            false
        }
    }

    private fun isFormingValidDate(date: String): Boolean {
        if (date.length > VALID_DATE_LENGTH) {
            return false
        }
        for (char in date) {
            if (!(char.isDigit() || char == DATE_TOKEN)) {
                return false
            }
        }
        return true
    }

    private fun isFormingValidPrice(price: String): Boolean {
        var isDecimal = false
        for (char in price) {
            if (char == ',' || char == '.') {
                if (isDecimal) {
                    return false
                }
                isDecimal = true
                continue
            }
            if (!char.isDigit()) {
                return false
            }
        }
        return true
    }
}