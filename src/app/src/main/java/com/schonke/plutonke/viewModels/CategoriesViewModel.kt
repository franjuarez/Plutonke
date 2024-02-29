package com.schonke.plutonke.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.schonke.plutonke.states.LoadDataState
import com.schonke.plutonke.types.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CategoriesViewModel(
    private val dataViewModel: SharedDataViewModel,
) : ViewModel() {

    private val _categoryName = MutableLiveData<String>()
    val categoryName: LiveData<String> = _categoryName

    private val _categorySpentAmount = MutableLiveData<String>()
    val categorySpentAmount: LiveData<String> = _categorySpentAmount

    private val _categoryMaxAmount = MutableLiveData<String>()
    val categoryMaxAmount: LiveData<String> = _categoryMaxAmount

    private val _categoryValidState = MutableStateFlow<LoadDataState>(LoadDataState.Loading)
    val categoryValidState: StateFlow<LoadDataState> = _categoryValidState


    fun onNameChanged(newName: String) {
        _categoryName.value = newName
    }

    fun onSpentAmountChanged(newAmount: String) {
        _categorySpentAmount.value = newAmount
    }
    fun onMaxAmountChanged(newAmount: String) {
        _categoryMaxAmount.value = newAmount
    }

    fun resetCategory() {
        _categoryName.value = ""
        _categoryMaxAmount.value = ""
    }

    fun onCategoryNameChanged(newName: String) {
        _categoryName.value = newName
    }

    fun onMaxChanged(newMax: String) {
        _categoryMaxAmount.value = newMax
    }

    fun onModifiedPressed(id: String) {
//        if (expenseHasNoNullFields()) {
//            val price = expensePrice.value?.replace(",", ".")
//                ?.toFloatOrNull() ?: 0.0f
//            dataViewModel.modifyExpense(
//                id,
//                Expense(
//                    id, expenseName.value!!,
//                    expenseDate.value!!,
//                    price,
//                    expenseCategory.value!!
//                ),
//                _expenseValidState
//            )
//        }
    }

}