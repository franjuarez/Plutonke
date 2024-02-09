package com.schonke.plutonke.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.schonke.plutonke.types.Expense
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    private fun resetExpense(){
        _expenseName.value = ""
        _expenseDate.value = ""
        _expensePrice.value = ""
        _expenseCategory.value = ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onConfirmPressed() : Boolean{
        if(isExpenseNameValid() && isExpenseDateValid() &&
            isExpensePriceValid() && isExpenseCategoryValid()){
            //TODO: ver q onda ID y que la Category sea del tipo Category
            val expense = Expense(
                "59", expenseName.value!!,
                expenseDate.value!!,
                expensePrice.value!!.toInt(),
                expenseCategory.value!!)

            dataViewModel.addExpense(expense)
            resetExpense()
            return true
        }
        return false
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
        if(isValidPrice(price)){
            _expensePrice.value = price
        }
    }

    fun onCategoryChanged(category: String) {
        //TODO: chequear que sea una categoria que exista
        //Estaria bueno hacer un dropdown en vez de que escriba lo que quiera
        _expenseCategory.value = category
    }

    //TODO: mejorar mecanismo de chequeo de fecha (si se puede usar DatePicker)
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

    private fun isExpenseNameValid() : Boolean{
        if(_expenseName.value == null) return false
        return _expenseName.value?.length!! > 0
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isExpenseDateValid() : Boolean{
        if(_expenseDate.value == null){
            return false
        }
        return try {
            val date = _expenseDate.value.toString()
            val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
            val localDate = LocalDate.parse(date, formatter)
            !(!formatter.format(localDate).equals(date) || localDate > LocalDate.now())
        } catch (e: Exception) {
            false
        }
    }

    private fun isExpensePriceValid() : Boolean{
        if(_expensePrice.value == null) return false
        return _expensePrice.value?.length!! > 0 && isValidPrice(_expensePrice.value.toString())
    }

    private fun isExpenseCategoryValid() : Boolean{
        if(_expenseCategory.value == null) return false
        return _expenseCategory.value?.length!! > 0
    }

    private fun isValidPrice(price: String) : Boolean{
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