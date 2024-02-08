package com.schonke.plutonke.types

class Category (
    val id: String,
    val name: String,
    val maxAmount: Int,
    //TODO: FALTA LO DE REINCIAR CADA X TIEMPO
    )
{
    var spentAmount = 0

    override fun toString(): String {
        return name
    }
}