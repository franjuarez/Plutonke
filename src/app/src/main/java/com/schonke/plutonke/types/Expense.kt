package com.schonke.plutonke.types

data class Expense(
    val id: String,
    val name: String,
    val date: String,
    val price: Int,
    val category: String)