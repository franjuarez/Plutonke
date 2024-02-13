package com.schonke.plutonke.states

sealed class LoadDataState {
    object Loading : LoadDataState()

    data class Success(val msg: String) : LoadDataState()

    data class Error(val msg: String) : LoadDataState()
}