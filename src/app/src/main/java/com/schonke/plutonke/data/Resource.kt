package com.schonke.plutonke.data

import com.schonke.plutonke.errors.ValidationError

sealed class Resource<T>(val data: T?, val message: String?,
                         val errorResponse: List<ValidationError>?) {
    class Success<T>(data: T) : Resource<T>(data, null,null)
    class ErrorValidation<T>(errorResponse: List<ValidationError>?) : Resource<T>(null, null,errorResponse)
    class Error<T>(message: String) : Resource<T>(null, message,null)
}