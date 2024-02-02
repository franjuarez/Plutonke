package com.schonke.plutonke

class Category (
    var name: String,
    var maxAmount: Int,
    //TODO: FALTA LO DE REINCIAR CADA X TIEMPO
    )
{
    var spentAmount = 0
    override fun toString(): String {
        return name
    }
}