package com.schonke.plutonke

data class Expense(
    val name: String,
    val date: String,
    val price: Int,
    val category: Category)