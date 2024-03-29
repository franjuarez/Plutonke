package com.schonke.plutonke.types

import com.google.gson.annotations.SerializedName

data class Expense(
    @SerializedName("id") val id: UInt,
    @SerializedName("name") val name: String,
    @SerializedName("date") var date: String,
    @SerializedName("price") val price: Float,
    @SerializedName("category_id") val categoryID: UInt)