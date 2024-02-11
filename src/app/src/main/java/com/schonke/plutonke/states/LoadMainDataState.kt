package com.schonke.plutonke.states

sealed class LoadMainDataState {
    object Loading : LoadMainDataState()

    object Success : LoadMainDataState()

    data class Error(val msg: String) : LoadMainDataState()
}