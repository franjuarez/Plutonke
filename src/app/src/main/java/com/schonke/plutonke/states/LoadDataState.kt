package com.schonke.plutonke.states

sealed class LoadDataState {
    object Loading : LoadDataState()

    object Success : LoadDataState()

    data class Error(val msg: String) : LoadDataState()
}