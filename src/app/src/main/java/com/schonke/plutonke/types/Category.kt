package com.schonke.plutonke.types

import com.google.gson.annotations.SerializedName

data class Category (
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("maxAmount") val maxAmount: Float,
    @SerializedName("spentAmount") var spentAmount: Float
    )
{
    override fun toString(): String {
        return name
    }
}