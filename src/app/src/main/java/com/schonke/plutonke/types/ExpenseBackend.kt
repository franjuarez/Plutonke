package com.schonke.plutonke.types

import com.google.gson.annotations.SerializedName

data class ExpenseBackend(
    @SerializedName("id") val id: UInt,
    @SerializedName("name") val name: String,
    @SerializedName("date") var date: Long,
    @SerializedName("price") val price: Float,
    @SerializedName("category_id") val categoryID: UInt)