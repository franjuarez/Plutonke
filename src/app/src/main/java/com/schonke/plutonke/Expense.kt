package com.schonke.plutonke

data class Expense(
    val id: String,
    val name: String,
    val date: String,
    val price: Int,
    val category: Category)