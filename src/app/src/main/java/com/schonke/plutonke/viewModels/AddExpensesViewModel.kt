package com.schonke.plutonke.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.schonke.plutonke.states.LoadMainDataState
import com.schonke.plutonke.types.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

const val DATE_FORMAT = "dd/MM/yyyy"
const val VALID_DATE_LENGTH = 10
const val DATE_TOKEN = '/'

class AddExpensesViewModel(private val dataViewModel: SharedDataViewModel) : ViewModel() {

    private val _expenseName = MutableLiveData<String>()
    val expenseName : LiveData<String> = _expenseName

    private val _expenseDate = MutableLiveData<String>()
    val expenseDate : LiveData<String> = _expenseDate

    private val _expensePrice = MutableLiveData<String>()
    val expensePrice : LiveData<String> = _expensePrice

    private val _expenseCategory = MutableLiveData<String>()
    val expenseCategory : LiveData<String> = _expenseCategory

    val expenseValidState: StateFlow<LoadMainDataState> get() = dataViewModel.expenseValidState

    fun resetExpense(){
        _expenseName.value = ""
        _expenseDate.value = ""
        _expensePrice.value = ""
        _expenseCategory.value = ""
        dataViewModel.resetExpenseValidState()
    }

    fun resetExpenseValidState(){
        dataViewModel.resetExpenseValidState()
    }

    private fun expenseHasNoNullFields(): Boolean{
        return !(_expenseName.value == null || _expensePrice.value == null ||
                _expenseDate.value == null || _expenseCategory.value == null)
    }

    fun onConfirmPressed() {
        if (expenseHasNoNullFields()) {
            dataViewModel.addExpense(
                expenseName.value!!,
                expenseDate.value!!,
                expensePrice.value!!,
                expenseCategory.value!!
            )
        }
    }

    fun onNameChanged(name: String) {
        _expenseName.value = name
    }

    fun onDateChanged(date: String) {
        if(isFormingValidDate(date)){
            _expenseDate.value = date
        }
    }

    fun onPriceChanged(price: String) {
        if(isFormingValidPrice(price)){
            _expensePrice.value = price
        }
    }

    fun onCategoryChanged(category: String) {
        _expenseCategory.value = category
    }

    private fun isFormingValidDate(date: String) : Boolean{
        if(date.length > VALID_DATE_LENGTH){
            return false
        }
        for(char in date){
            if(!(char.isDigit() || char == DATE_TOKEN)){
                return false
            }
        }
        return true
    }
    private fun isFormingValidPrice(price: String) : Boolean{
        var isDecimal = false
        for(char in price){
            if(char == ','){
                if(isDecimal){
                    return false
                }
                isDecimal = true
                continue
            }
            if(!char.isDigit()){
                return false
            }
        }
        return true
    }
}