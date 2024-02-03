package com.schonke.plutonke.components

import androidx.lifecycle.ViewModel
import com.schonke.plutonke.Category

class CategoriesViewModel: ViewModel() {

    var categories: List<Category>? = null
        private set


    fun init() {
        // aca cargar categories con la base de datos
        // por ahora la hacemos a mano

        val comida = Category(name = "a", maxAmount = 50000)
        val diversion = Category(name = "Diversionaaaaaaa", maxAmount = 150000)
        val salidas = Category(name = "Salidas", maxAmount = 200000)
        val cumpleanios_de_urko = Category(name = "Cumples de Urko", maxAmount = 999999)
        val otra_categoria = Category(name = "otra categoria", maxAmount = 10000)
        val steam = Category(name = "steam", maxAmount = 25000)
        val ultima = Category(name = "ultima", maxAmount = 20000)
        val ultima2 = Category(name = "ultima2", maxAmount = 20001)
        categories = listOf(
            comida,
            diversion,
            salidas,
            cumpleanios_de_urko,
            otra_categoria,
            steam,
            ultima,
            ultima2
        )
        println("init")
    }


}