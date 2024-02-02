package com.schonke.plutonke.components

import androidx.lifecycle.ViewModel
import com.schonke.plutonke.Category

class CategoryViewModel(): ViewModel() {

    fun updateCategoryProperties(category: Category, newName: String? = null, newSpentAmount: Int? = null, newMaxAmount: Int? = null) {
        newName?.let { category.name = it }
        newSpentAmount?.let { category.spentAmount = it }
        newMaxAmount?.let { category.maxAmount = it }

        // Por último, puedes guardar la categoría modificada si es necesario
        // (por ejemplo, si estás utilizando un repositorio o una base de datos)
        // repository.guardarCategoria(categoria)
        println(category.name)
        println(category.spentAmount)
        println(category.maxAmount)
    }
}