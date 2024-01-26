package com.schonke.plutonke

class Category (
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