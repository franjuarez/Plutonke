package com.schonke.plutonke.errors

import com.google.gson.annotations.SerializedName

data class ValidationError(
    @SerializedName("field") val field: String,
    @SerializedName("message") val message: String
)

