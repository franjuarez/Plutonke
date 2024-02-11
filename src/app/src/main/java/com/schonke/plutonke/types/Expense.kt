package com.schonke.plutonke.types

import com.google.gson.annotations.SerializedName

data class Expense(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("date") val date: String,
    @SerializedName("price") val price: Float,
    @SerializedName("category") val category: String)