package com.schonke.plutonke.types

import com.google.gson.annotations.SerializedName

data class Category (
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("maxAmount") val maxAmount: Int,
    @SerializedName("spentAmount") var spentAmount: Int
    )
{
    override fun toString(): String {
        return name
    }
}