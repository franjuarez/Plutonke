package com.schonke.plutonke.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.schonke.plutonke.errors.ValidationError
import com.schonke.plutonke.screens.CATEGORY_MAX_AMOUNT_FIELD_ERR
import com.schonke.plutonke.screens.CATEGORY_NAME_FIELD_ERR
import com.schonke.plutonke.states.LoadDataState
import com.schonke.plutonke.types.Category
import com.schonke.plutonke.types.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val REQUIERED_FIELD = "This field can't be empty!"

class CategoriesViewModel(
    private val dataViewModel: SharedDataViewModel,
) : ViewModel() {

    private val _categoryName = MutableLiveData<String>()
    val categoryName: LiveData<String> = _categoryName

    private val _categoryMaxAmount = MutableLiveData<String>()
    val categoryMaxAmount: LiveData<String> = _categoryMaxAmount

    private val _categoryValidState = MutableStateFlow<LoadDataState>(LoadDataState.Loading)
    val categoryValidState: StateFlow<LoadDataState> = _categoryValidState

    fun resetCategory() {
        _categoryName.value = ""
        _categoryMaxAmount.value = ""
        resetCategoryValidState()
    }

    fun resetCategoryValidState() {
        _categoryValidState.value = LoadDataState.Loading
    }

    private fun categoryHasInvalidFields(): Boolean {
        return (_categoryName.value == null || _categoryName.value == "" ||
                _categoryMaxAmount.value == null || _categoryMaxAmount.value == ""
                )
    }

    private fun showInvalidFieldError() {
        val validationErrors = mutableListOf<ValidationError>()
        if (_categoryName.value == null || _categoryName.value == "") {
            val validationError = ValidationError(CATEGORY_NAME_FIELD_ERR, REQUIERED_FIELD)
            validationErrors.add(validationError)
        }
        if (_categoryMaxAmount.value == null || _categoryMaxAmount.value == "") {
            val validationError = ValidationError(CATEGORY_MAX_AMOUNT_FIELD_ERR, REQUIERED_FIELD)
            validationErrors.add(validationError)
        }
        _categoryValidState.value = LoadDataState.ErrorValidating(validationErrors)
    }

    private fun formatMaxAmountPrice(): Float {
        val price = _categoryMaxAmount.value?.replace(",", ".")
            ?.toFloatOrNull() ?: 0.0f
        return price
    }

    fun onConfirmPressed() {
        if (!categoryHasInvalidFields()) {
            val price = formatMaxAmountPrice()
            dataViewModel.addCategory(
                Category(
                    0U,
                    categoryName.value!!,
                    price,
                    0F
                ), _categoryValidState
            )
        } else {
            showInvalidFieldError()
        }
    }

    fun onModifiedPressed(id: UInt) {
        if (!categoryHasInvalidFields()) {
            val price = formatMaxAmountPrice()
            dataViewModel.modifyCategory(
                id,
                Category(
                    id, categoryName.value!!,
                    price,
                    dataViewModel.sharedCategories.value?.find {it.id == id}?.spentAmount!!
                ),
                _categoryValidState
            )
        } else {
            showInvalidFieldError()
        }
    }

    fun onDeletePressed(id: UInt) {
        dataViewModel.removeCategory(id, _categoryValidState)
    }

    fun onNameChanged(name: String) {
        println(name)
        _categoryName.value = name
    }

    fun onMaxAmountChanged(price: String) {
        if (isFormingValidPrice(price)) {
            _categoryMaxAmount.value = price
        }
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