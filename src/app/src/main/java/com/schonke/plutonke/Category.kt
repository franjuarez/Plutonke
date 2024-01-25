package com.schonke.plutonke

class Category (
    val name: String,
    val spentAmount: Int,
    val maxAmount: Int,
    //TODO: FALTA LO DE REINCIAR CADA X TIEMPO
    )
{
    override fun toString(): String {
        return name
    }
}