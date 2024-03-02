package com.schonke.plutonke.states

import com.schonke.plutonke.errors.ValidationError

sealed class LoadDataState {
    data object Loading : LoadDataState()
    data class Success(val msg: String) : LoadDataState()
    data class Error(val msg: String) : LoadDataState()
    data class ErrorValidating(val errors: List<ValidationError>) : LoadDataState()
}

